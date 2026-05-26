package com.example.library_management.service;

import com.example.library_management.config.JwtUtil;
import com.example.library_management.dto.LoginRequest;
import com.example.library_management.dto.LoginResponse;
import com.example.library_management.dto.RegisterRequest;
import com.example.library_management.dto.UserResponse;
import com.example.library_management.exception.AuthException;
import com.example.library_management.model.Role;
import com.example.library_management.model.User;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

//    public LoginResponse login(LoginRequest request) {
//        // authenticate — throws exception if wrong credentials
//        Authentication auth = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getUsername(),
//                        request.getPassword()
//                )
//        );
//
//        // generate JWT token
//        String token = jwtUtil.generateToken(auth.getName());
//
//        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
//
//        return new LoginResponse(token, user.getUsername(), user.getRole().name());
//    }

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
            return new LoginResponse(token, user.getUsername(), user.getRole().name());

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new AuthException("Invalid username or password");
        }
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

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

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}