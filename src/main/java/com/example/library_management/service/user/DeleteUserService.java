package com.example.library_management.service.user;

import com.example.library_management.dto.request.UserRequest;
import com.example.library_management.model.User;
import com.example.library_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserService {
    @Autowired
    UserRepository userRepository;

    public String deleteUser(UserRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        return "User @"+ user.getUsername() + " is deleted";
    }
}
