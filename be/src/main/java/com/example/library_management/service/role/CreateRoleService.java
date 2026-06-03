package com.example.library_management.service.role;

import com.example.library_management.dto.request.RoleRequest;
import com.example.library_management.dto.response.RoleResponse;
import com.example.library_management.model.Role;
import com.example.library_management.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateRoleService {
    @Autowired
    RoleRepository roleRepository;

    public RoleResponse createRole(RoleRequest request){
        if(roleRepository.existsByName(request.getName())){
            throw new RuntimeException("Role already existed");
        }
        else{
            Role role = new Role();
            role.setName(request.getName());
            Role savedRole = roleRepository.save(role);
            log.info("Creating role {}, ID #{}", savedRole.getName(), savedRole.getId());
            return new RoleResponse(savedRole);
        }
    }
}
