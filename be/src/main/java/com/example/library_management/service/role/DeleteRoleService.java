package com.example.library_management.service.role;

import com.example.library_management.dto.request.role.RoleRequest;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Role;
import com.example.library_management.model.User;
import com.example.library_management.repository.RoleRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DeleteRoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    @Transactional
    public String deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ROLE-NOT-FOUND", messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));
        if (role.getName().equals("ROLE_ROOT")) {
            throw new ApiException(HttpStatus.CONFLICT, "ROOT-ROLE-CONFLICT", messageSource.getMessage("error.cannot.delete.root.role", null, LocaleContextHolder.getLocale()));
        }
        if (role.getIsDefault()) {
            throw new ApiException(
                    HttpStatus.CONFLICT, "DEFAULT-ROLE-CONFLICT", messageSource.getMessage("error.cannot.delete.default.role", null, LocaleContextHolder.getLocale())
            );
        }
        List<User> users = userRepository.findByRole_NameAndIsDeletedFalse(role.getName());
        if (!users.isEmpty()){
            Role defaultRole = roleRepository.findByIsDefaultIsTrue().orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ROLE-NOT-FOUND", messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));
            users.forEach(user -> user.setRole(defaultRole));
            userRepository.saveAll(users);
            logger.log("Update all users with role {} to role {}", role.getName(), defaultRole.getName());
        }
        role.getFeatures().clear();
        roleRepository.delete(role);
        String message = messageSource.getMessage("role.delete", null, LocaleContextHolder.getLocale());
        logger.log("Deleted {}, ID #{}", role.getName(), role.getId());
        return message + " " + role.getName() + " | ID #" + role.getId();
    }
}