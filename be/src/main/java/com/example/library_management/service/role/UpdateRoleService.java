package com.example.library_management.service.role;

import com.example.library_management.dto.request.role.RoleRequest;
import com.example.library_management.dto.response.RoleResponse;
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
public class UpdateRoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public RoleResponse updateRole(RoleRequest request){
        Role role = roleRepository.findById(request.getId()).orElseThrow(()->new RuntimeException(messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));
        String originalName = role.getName();
        role.setName(request.getName());
        if(request.getIsDefault()) {
            Role defaultRole = roleRepository.findByIsDefaultIsTrue().orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.role.not.found", null, LocaleContextHolder.getLocale())));
            defaultRole.setIsDefault(false);
            roleRepository.save(defaultRole);
            role.setIsDefault(request.getIsDefault());
            logger.log("Update {} default = {}", defaultRole.getName(), defaultRole.getIsDefault());
        }
        Role savedRole = roleRepository.save(role);
        logger.log("Updated {} -> {}, ID #{}",originalName, savedRole.getName(), savedRole.getId());
        return new RoleResponse(savedRole);
    }
}
