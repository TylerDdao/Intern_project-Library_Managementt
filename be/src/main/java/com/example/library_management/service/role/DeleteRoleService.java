package com.example.library_management.service.role;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.dto.request.RoleRequest;
import com.example.library_management.model.Book;
import com.example.library_management.model.Role;
import com.example.library_management.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeleteRoleService {
    @Autowired
    private RoleRepository roleRepository;

    public String deleteRole(RoleRequest request){
        Role role = roleRepository.findByName(request.getName())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if(role.getName().equals("ROLE_ROOT")){
            throw new RuntimeException("Can not delete root");
        }
        roleRepository.delete(role);
        log.info("Deleting role {}, ID #{}", role.getName(), role.getId());
        return "Role "+ role.getName() + " | ID #" + role.getId() + " is deleted";
    }
}
