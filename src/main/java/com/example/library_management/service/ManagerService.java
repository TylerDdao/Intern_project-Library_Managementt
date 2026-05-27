package com.example.library_management.service;

import com.example.library_management.dto.UserResponse;
import com.example.library_management.model.User;
import com.example.library_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {

    @Autowired
    private UserRepository userRepository;

    public Page<UserResponse> getUsers(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<User> users = userRepository.findAll(pageable);

        return users.map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getPhoneNumber(),
                user.getFullName(),
                user.getAddress(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        ));
    }
}