package com.example.library_management.service.user;

import com.example.library_management.dto.UserRequest;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.model.Role;
import com.example.library_management.model.User;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public UserResponse updateUserRole(UserRequest request){
        try {
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

            userRepository.save(user);

            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().getName(),
                    user.getPhoneNumber(),
                    user.getFullName(),
                    user.getAddress(),
                    user.getEmail(),
                    null,
                    null
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

    public UserResponse updateUser(UserRequest request){
        try {

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));


            // update only the fields that should change
            if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
            if (request.getAddress() != null) user.setAddress(request.getAddress());
            if (request.getEmail() != null) user.setEmail(request.getEmail());
            if (request.getUsername() != null) user.setUsername(request.getUsername());
            if (request.getFullName() != null) user.setFullName(request.getFullName());

            userRepository.save(user);

            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().getName(),
                    user.getPhoneNumber(),
                    user.getFullName(),
                    user.getAddress(),
                    user.getEmail(),
                    null,
                    null
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
}
