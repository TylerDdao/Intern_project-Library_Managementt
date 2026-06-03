package com.example.library_management.service.user;

import com.example.library_management.dto.request.UserRequest;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.model.Role;
import com.example.library_management.model.User;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateUserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @PreAuthorize("@securityService.hasAccess('ROLE_ROOT')")
    public UserResponse updateUserRole(UserRequest request){
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = roleRepository.findByName(request.getRole())
                .orElse(defaultRole);

        // update only the fields that should change
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getRole() != null) user.setRole(newRole);
        if (request.getFullName() != null) user.setFullName(request.getFullName());

        User savedUser = userRepository.save(user);
        log.info("Updating role @{}, ID #{} to {}", savedUser.getUsername(),savedUser.getId(), savedUser.getRole().getName());
        return new UserResponse(savedUser);
    }

    public UserResponse updateUser(UserRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));


        // update only the fields that should change
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getFullName() != null) user.setFullName(request.getFullName());

        User savedUser = userRepository.save(user);
        log.info("Updating @{}, ID #{}", savedUser.getUsername(),savedUser.getId());
        return new UserResponse(savedUser);
    }
}
