package com.example.library_management.service.user;

import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.model.Role;
import com.example.library_management.model.User;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse updateUserRole(UserRequest request){
        System.out.println(request);

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));

        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.user.id.not.found", null, LocaleContextHolder.getLocale())));

        Role newRole = roleRepository.findById(request.getRole())
                .orElse(defaultRole);


        // update only the fields that should change
        if (request.getRole() != null) user.setRole(newRole);

        User savedUser = userRepository.save(user);
        System.out.println(request);
        logger.log("Updated role for @{}, ID #{} to {}", savedUser.getUsername(),savedUser.getId(), savedUser.getRole().getName());
        return new UserResponse(savedUser);
    }

    public UserResponse updateUser(UserRequest request){
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.username.not.found", null, LocaleContextHolder.getLocale())));

        // update only the fields that should change
        if(request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPassword() != null){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User savedUser = userRepository.save(user);
        logger.log("Updated @{}, ID #{}", savedUser.getUsername(),savedUser.getId());
        return new UserResponse(savedUser);
    }
}
