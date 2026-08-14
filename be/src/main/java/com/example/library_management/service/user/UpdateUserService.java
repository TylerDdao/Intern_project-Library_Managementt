package com.example.library_management.service.user;

import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.UserResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Role;
import com.example.library_management.model.User;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.service.mail.UserMailService;
import com.example.library_management.util.AuditLogger;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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

    @Autowired
    private UserMailService userMailService;

    public UserResponse updateUserRole(UserRequest request) {
        Role defaultRole = roleRepository.findByIsDefaultIsTrue().orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));
        User user = userRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.user.id.not.found", null, LocaleContextHolder.getLocale())));

        Role newRole = request.getRole() != null ? roleRepository.findById(request.getRole()).orElse(defaultRole) : defaultRole;

        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        boolean isRoot = user.getRole().getName().equals("ROLE_ROOT");

        boolean changingRole = user.getRole().getId() != newRole.getId();

        if (changingRole && user.getUsername().equals(username) && isRoot) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ROOT-USER", messageSource.getMessage("error.cannot.delete.root.user", null, LocaleContextHolder.getLocale()));
        }

        if (changingRole && isRoot && !newRole.getName().equals("ROLE_ROOT")) {
            long rootCount = userRepository.countByRole_NameAndIsDeletedFalse("ROLE_ROOT");

            if (rootCount <= 1) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "ROOT-USER", messageSource.getMessage("error.cannot.remove.last.root.user", null, LocaleContextHolder.getLocale()));
            }

            user.setRole(newRole);
            User savedUser = userRepository.save(user);
            logger.log("Updated role for @{}, ID #{} to {}", savedUser.getUsername(), savedUser.getId(), savedUser.getRole().getName());
            return new UserResponse(savedUser);
        }
        else{
            user.setRole(newRole);
            User savedUser = userRepository.save(user);
            logger.log("Updated role for @{}, ID #{} to {}", savedUser.getUsername(), savedUser.getId(), savedUser.getRole().getName());
            return new UserResponse(savedUser);
        }
    }

    @Transactional
    public UserResponse updateUserSelf(UserRequest request) throws MessagingException {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
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
        if(request.getPassword()!=null){
            try {
                userMailService.sendPasswordChangedEmail(savedUser);
            }
            catch (Exception e){
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ERROR", e.getMessage());
            }
        }
        logger.log("Updated @{}, ID #{}", savedUser.getUsername(),savedUser.getId());
        return new UserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(UserRequest request) throws MessagingException {
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
        if(request.getPassword()!=null){
            try {
                userMailService.sendPasswordChangedEmail(savedUser);
            }
            catch (Exception e){
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ERROR", e.getMessage());
            }
        }
        logger.log("Updated @{}, ID #{}", savedUser.getUsername(),savedUser.getId());
        return new UserResponse(savedUser);
    }
}
