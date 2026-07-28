package com.example.library_management.service.user;

import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.model.User;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeleteUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public String deleteUser(UserRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.username.not.found", null, LocaleContextHolder.getLocale())));
        if(user.getRole().getName().equals("ROLE_ROOT")){
            String message = messageSource.getMessage("error.cannot.delete.root.user", null, LocaleContextHolder.getLocale());
            throw new RuntimeException(message);
        }
        userRepository.delete(user);
        String message = messageSource.getMessage("user.delete", null, LocaleContextHolder.getLocale());
        logger.log("Deleted @{}", user.getUsername());
        return message + "@"+ user.getUsername();
    }
}
