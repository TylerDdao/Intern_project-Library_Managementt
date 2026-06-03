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
public class UpdateRoleService {
    @Autowired
    RoleRepository roleRepository;

    public RoleResponse updateRole(String name, RoleRequest request){
        Role role = roleRepository.findByName(name).orElseThrow(()->new RuntimeException("No role found"));
        role.setName(request.getName());
        Role savedRole = roleRepository.save(role);
        log.info("Updating role {}, ID #{}", savedRole.getName(), savedRole.getId());
        return new RoleResponse(savedRole);
    }
}
