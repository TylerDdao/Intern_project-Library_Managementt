package com.example.library_management.service.user;

import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.user.UserResponse;
import com.example.library_management.model.Role;
import com.example.library_management.model.User;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    public UserResponse createUser(UserRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if(request.getRole() != null){
            Role role = roleRepository.findById(request.getRole()).orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));
            user.setRole(role);
        }
        else {
            Role role = roleRepository.findByIsDefaultIsTrue().orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));
            user.setRole(role);
        }

        user = userRepository.save(user);
        logger.log("Create user @{} ID #{}", user.getUsername(), user.getId());

        return new UserResponse(user);
    }
}
