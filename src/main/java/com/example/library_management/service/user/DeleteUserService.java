package com.example.library_management.service.user;

import com.example.library_management.dto.UserRequest;
import com.example.library_management.model.User;
import com.example.library_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserService {
    @Autowired
    UserRepository userRepository;

    public String deleteUser(UserRequest request){
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            userRepository.delete(user);

            return "User @"+ user.getUsername() + "deleted";
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
