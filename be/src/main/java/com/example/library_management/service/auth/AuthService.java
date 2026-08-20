package com.example.library_management.service.auth;

import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Feature;
import com.example.library_management.model.ResetPasswordCode;
import com.example.library_management.repository.FeatureRepository;
import com.example.library_management.repository.ResetPasswordCodeRepository;
import com.example.library_management.service.TokenBlacklistService;
import com.example.library_management.service.mail.UserMailService;
import com.example.library_management.util.AuditLogger;
import com.example.library_management.util.JwtUtil;
import com.example.library_management.dto.request.auth.LoginRequest;
import com.example.library_management.dto.request.auth.RegisterRequest;
import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.auth.LoginResponse;
import com.example.library_management.dto.response.user.UserResponse;
import com.example.library_management.exception.AuthException;
import com.example.library_management.model.Role;
import com.example.library_management.model.User;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
    private AuditLogger logger;

    @Autowired
    private UserMailService userMailService;

    @Autowired
    private ResetPasswordCodeRepository resetPasswordCodeRepository;

    @Autowired
    private TurnstileService turnstileService;

    public boolean resetPassword(UserRequest request){
        User user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND, "USER-NOT-FOUND", messageSource.getMessage("error.user.not.found", null, LocaleContextHolder.getLocale())));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        ResetPasswordCode resetCode = resetPasswordCodeRepository.findTopByUser_EmailOrderByCreatedAtDesc(request.getEmail()).orElseThrow();
        resetCode.setReset(true);
        resetPasswordCodeRepository.save(resetCode);
        logger.log("SYSTEM", "Password reset for @{}", user.getUsername());
        return true;
    }

    public boolean verifyPassword(LoginRequest request) {
        try {
            User user = userRepository.findByUsernameAndIsDeletedFalse(request.getUsername()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER-NOT-FOUND",  messageSource.getMessage("error.user.not.found", null, LocaleContextHolder.getLocale())));
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String message = messageSource.getMessage("error.incorrect.credential", null, LocaleContextHolder.getLocale());
                throw new AuthException(message);
            }
            return true;
        } catch (AuthException e) {
            return false;
        }
    }

    public LoginResponse login(LoginRequest request) {
        if (!turnstileService.verify(request.getTurnstileToken())) {
            String message = messageSource.getMessage("error.captcha.failed", null, LocaleContextHolder.getLocale());
            logger.warn("Unable to verify capcha for login attempt on @{}", request.getUsername());
            throw new AuthException(message);
        }

        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            String token = jwtUtil.generateToken(auth.getName());
            User user = userRepository.findByUsernameAndIsDeletedFalse(auth.getName()).orElseThrow();
            List<Feature> authorities = featureRepository.findByRoles_Id(user.getRole().getId());
            logger.log("Authorized @{}, ID #{}", user.getUsername(), user.getId());
            return new LoginResponse(user, token, authorities);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            String message = messageSource.getMessage("error.incorrect.credential", null, LocaleContextHolder.getLocale());
            throw new AuthException(message);
        }
    }

    public UserResponse updateAccount(UserRequest request){
        User user = userRepository.findByUsernameAndIsDeletedFalse(request.getUsername()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER-NOT-FOUND", messageSource.getMessage("error.user.not.found", null, LocaleContextHolder.getLocale())));

        // update only the fields that should change
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        logger.log("Updated @{}, ID #{}", savedUser.getUsername(), savedUser.getId());
        return new UserResponse(savedUser);
    }

    public String logout(String token){
        tokenBlacklistService.blacklist(token);
        String username = jwtUtil.extractUsername(token);
        SecurityContextHolder.clearContext();
        logger.log("Logged out @{}", username);
        return messageSource.getMessage("auth.logout.success", null, LocaleContextHolder.getLocale());
    }

    public UserResponse register(RegisterRequest request) {
        if (!turnstileService.verify(request.getTurnstileToken())) {
            String message = messageSource.getMessage("error.captcha.failed", null, LocaleContextHolder.getLocale());
            logger.warn("Unable to verify capcha for register attempt");
            throw new AuthException(message);
        }
        if (userRepository.existsByUsernameAndIsDeletedFalse(request.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "USERNAME-TAKEN",messageSource.getMessage("error.username.taken", null, LocaleContextHolder.getLocale()));
        }
        Role defaultRole = roleRepository.findByIsDefaultIsTrue().orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ROLE-NOT-FOUND", messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(defaultRole);

        User savedUser = userRepository.save(user);
        logger.log("SYSTEM", "Registered @{}, ID #{}", savedUser.getUsername(), savedUser.getId());
        userMailService.sendWelcomeEmail(savedUser);
        return new UserResponse(savedUser);
    }

    public UserResponse getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            throw new AuthException(messageSource.getMessage("error.user.not.authenticated", null, LocaleContextHolder.getLocale()));
        }

        String username = auth.getName();

        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.user.not.found", null, LocaleContextHolder.getLocale())));

        return new UserResponse(user);
    }
}