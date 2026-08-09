package com.example.library_management.service.user;

import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.User;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.service.MailService;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DeleteUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    @Autowired
    MailService mailService;

    @Transactional
    public String deleteUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.username.not.found", null, LocaleContextHolder.getLocale())));
        if(user.getRole().getName().equals("ROLE_ROOT")){
            String message = messageSource.getMessage("error.cannot.delete.root.user", null, LocaleContextHolder.getLocale());
            throw new ApiException(HttpStatus.BAD_REQUEST, "ROOT-USER" ,message);
        }

        if (borrowRepository.existsByUser_UsernameAndIsActiveTrue(username)){
            String message = messageSource.getMessage("error.cannot.delete.user.with.on.going.borrows", null, LocaleContextHolder.getLocale());
            throw new ApiException(HttpStatus.BAD_REQUEST, "USER-WITH-BORROWS" ,message);
        }

        user.setIsDeleted(true);
        userRepository.save(user);
        String message = messageSource.getMessage("user.delete", null, LocaleContextHolder.getLocale());
        logger.log("Deleted @{}", user.getUsername());
        mailService.sendAccountDeletedEmail(user);
        return message + "@"+ user.getUsername();
    }

    @Transactional
    public String deleteUser(Long id){
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.username.not.found", null, LocaleContextHolder.getLocale())));
        if(user.getRole().getName().equals("ROLE_ROOT")){
            String message = messageSource.getMessage("error.cannot.delete.root.user", null, LocaleContextHolder.getLocale());
            throw new ApiException(HttpStatus.BAD_REQUEST, "ROOT-USER" ,message);
        }

        if (borrowRepository.existsByUser_IdAndIsActiveTrue(id)){
            String message = messageSource.getMessage("error.cannot.delete.user.with.on.going.borrows", null, LocaleContextHolder.getLocale());
            throw new ApiException(HttpStatus.BAD_REQUEST, "USER-WITH-BORROWS" ,message);
        }

        user.setIsDeleted(true);
        userRepository.save(user);
        String message = messageSource.getMessage("user.delete", null, LocaleContextHolder.getLocale());
        logger.log("Deleted @{}", user.getUsername());
        mailService.sendAccountDeletedEmail(user);
        return message + "@"+ user.getUsername();
    }
}
