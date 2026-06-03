package com.example.library_management.service.user;

import com.example.library_management.dto.request.UserRequest;
import com.example.library_management.model.User;
import com.example.library_management.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeleteUserService {
    @Autowired
    UserRepository userRepository;

    public String deleteUser(UserRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(user.getRole().getName().equals("ROLE_ROOT")){
            throw new RuntimeException("Can not delete root user");
        }
        userRepository.delete(user);
        log.info("Deleting @{}", user.getUsername());
        return "User @"+ user.getUsername() + " is deleted";
    }
}
