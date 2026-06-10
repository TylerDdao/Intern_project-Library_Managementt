package com.example.library_management.service.role;

import com.example.library_management.dto.request.RoleRequest;
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
public class CreateRoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public RoleResponse createRole(RoleRequest request){
        if(roleRepository.existsByName(request.getName())){
            throw new RuntimeException(messageSource.getMessage("error.role.existed", null, LocaleContextHolder.getLocale()));
        }
        else{
            Role role = new Role();
            role.setName(request.getName());
            Role savedRole = roleRepository.save(role);
            logger.log("Created {}, ID #{}", savedRole.getName(), savedRole.getId());
            return new RoleResponse(savedRole);
        }
    }
}
