package com.example.library_management.service.role;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.dto.request.RoleRequest;
import com.example.library_management.model.Book;
import com.example.library_management.model.Role;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeleteRoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public String deleteRole(RoleRequest request){
        Role role = roleRepository.findByName(request.getName())
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));
        if(role.getName().equals("ROLE_ROOT")){
            throw new RuntimeException(messageSource.getMessage("error.cannot.delete.root.user", null, LocaleContextHolder.getLocale()));
        }
        roleRepository.delete(role);
        String message = messageSource.getMessage("role.delete", null, LocaleContextHolder.getLocale());
        logger.log("Deleted {}, ID #{}", role.getName(), role.getId());
        return message + " " + role.getName() + " | ID #" + role.getId();
    }
}
