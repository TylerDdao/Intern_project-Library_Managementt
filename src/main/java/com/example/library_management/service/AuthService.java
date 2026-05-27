package com.example.library_management.service;

import com.example.library_management.config.JwtUtil;
import com.example.library_management.dto.*;
import com.example.library_management.exception.AuthException;
import com.example.library_management.model.Role;
import com.example.library_management.model.User;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

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
            return new LoginResponse(token, user.getUsername(), user.getRole().getName());

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new AuthException("Invalid username or password");
        }
    }

    public AccountUpdateResponse updateAccount(AccountUpdateRequest request){
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Role newRole = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            // update only the fields that should change
            if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
            if (request.getAddress() != null) user.setAddress(request.getAddress());
            if (request.getEmail() != null) user.setEmail(request.getEmail());
            if (request.getUsername() != null) user.setUsername(request.getUsername());
            if (request.getRole() != null) user.setRole(newRole);
            if (request.getFullName() != null) user.setFullName(request.getFullName());

            userRepository.save(user);

            return new AccountUpdateResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().getName(),
                    user.getPhoneNumber(),
                    user.getFullName(),
                    user.getAddress(),
                    user.getEmail()
            );
        }
        catch (org.springframework.security.access.AccessDeniedException e) {
            throw new RuntimeException("Access denied");
        }
        catch (jakarta.persistence.EntityNotFoundException e) {
            throw new RuntimeException("User not found");
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Update failed: " + e.getMessage());
        }
    }

    public void logout(String token){
        tokenBlacklistService.blacklist(token);
        SecurityContextHolder.clearContext();
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Default Role 'ROLE_USER' not found in the database."));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(defaultRole);

        userRepository.save(user);
        return "User registered successfully";
    }

    public UserResponse getCurrentUser() {
        // get username from SecurityContext (set by JwtAuthFilter)
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //No use but this is how you check a role without @PreAuthorize
        boolean isUser = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> "ROLE_USER".equals(a.getAuthority()));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().getName(),
                user.getPhoneNumber(),
                user.getFullName(),
                user.getAddress(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}