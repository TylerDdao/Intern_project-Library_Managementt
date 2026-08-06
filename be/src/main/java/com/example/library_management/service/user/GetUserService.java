package com.example.library_management.service.user;

import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.model.User;
import com.example.library_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GetUserService {

    @Autowired
    private UserRepository userRepository;

    public Boolean checkUsername(String username){
        return userRepository.findByUsernameAndIsDeletedFalse(username).isEmpty();
    }

    public Page<UserResponse> getUsers(int page, int limit, String sortBy, String sortDir, String username, String fullName, String role) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<User> users;

        if (username != null) {
            users = userRepository.findByUsernameContainingAndIsDeletedFalse(username, pageable);
        } else if (fullName != null) {
            users = userRepository.findByFullNameContainingAndIsDeletedFalse(fullName, pageable);
        } else if (role != null) {
            users = userRepository.findByRole_NameContainingAndIsDeletedFalse(role, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(UserResponse::new);
    }
}