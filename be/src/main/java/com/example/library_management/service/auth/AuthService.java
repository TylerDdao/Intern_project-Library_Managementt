package com.example.library_management.service.auth;

import com.example.library_management.model.Feature;
import com.example.library_management.repository.FeatureRepository;
import com.example.library_management.service.TokenBlacklistService;
import com.example.library_management.util.AuditLogger;
import com.example.library_management.util.JwtUtil;
import com.example.library_management.dto.request.LoginRequest;
import com.example.library_management.dto.request.auth.RegisterRequest;
import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.LoginResponse;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.exception.AuthException;
import com.example.library_management.model.Role;
import com.example.library_management.model.User;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    AuditLogger logger;

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            String token = jwtUtil.generateToken(auth.getName());
            User user = userRepository.findByUsername(auth.getName()).orElseThrow();
            List<Feature> authorities = featureRepository.findByRoles_Id(user.getRole().getId());
            System.out.println(authorities);
            logger.log("SYSTEM","Authorized @{}, ID #{}", user.getUsername(), user.getId());
            return new LoginResponse(user, token, authorities);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            String message = messageSource.getMessage("error.invalid.credential", null, LocaleContextHolder.getLocale());
            throw new AuthException(message);
        }
    }

    public UserResponse updateAccount(UserRequest request){
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.user.not.found", null, LocaleContextHolder.getLocale())));

//            Role newRole = roleRepository.findByName(request.getRoleId())
//                    .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));

            // update only the fields that should change
            if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
            if (request.getAddress() != null) user.setAddress(request.getAddress());
            if (request.getEmail() != null) user.setEmail(request.getEmail());
            if (request.getUsername() != null) user.setUsername(request.getUsername());
//            if (request.getRoleId() != null) user.setRole(newRole);
            if (request.getFullName() != null) user.setFullName(request.getFullName());
            if (request.getPassword() != null) user.setPassword(request.getPassword());

            User savedUser = userRepository.save(user);
            logger.log("Updated @{}, ID #{}", savedUser.getUsername(), savedUser.getId());
            return new UserResponse(savedUser);
        }
        catch (org.springframework.security.access.AccessDeniedException e) {
            throw new RuntimeException(messageSource.getMessage("auth.access.denied", null, LocaleContextHolder.getLocale()));
        }
        catch (jakarta.persistence.EntityNotFoundException e) {
            throw new RuntimeException(messageSource.getMessage("error.user.not.found", null, LocaleContextHolder.getLocale()));
        }
        catch (RuntimeException e) {
            throw new RuntimeException(messageSource.getMessage("error.runtime", null, LocaleContextHolder.getLocale()) + e.getMessage());
        }
    }

    public void logout(String token){
        tokenBlacklistService.blacklist(token);
        String username = jwtUtil.extractUsername(token);
        SecurityContextHolder.clearContext();
        logger.log(username,"Logged out @{}", username);
    }

    public UserResponse register(RegisterRequest request, Locale locale) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException(messageSource.getMessage("error.username.taken", null, LocaleContextHolder.getLocale()));
        }

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.fount", null, LocaleContextHolder.getLocale())));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(defaultRole);

        UserRequest userRequest = new UserRequest(user);

        verificationService.sendVerificationEmail(userRequest, locale);

        User savedUser = userRepository.save(user);
        logger.log("SYSTEM","Registered @{}, ID #{}", savedUser.getUsername(), savedUser.getId());
        return new UserResponse(savedUser);
    }

    public UserResponse getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException(messageSource.getMessage("error.user.not.authenticated", null, LocaleContextHolder.getLocale()));
        }

        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.user.not.found", null, LocaleContextHolder.getLocale())));

        return new UserResponse(user);
    }
}