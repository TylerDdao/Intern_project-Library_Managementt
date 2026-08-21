package com.example.library_management.service.role;

import com.example.library_management.dto.response.role.RoleResponse;
import com.example.library_management.model.Feature;
import com.example.library_management.model.Role;
import com.example.library_management.repository.FeatureRepository;
import com.example.library_management.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FeatureRepository featureRepository;

    public Page<RoleResponse> getRole(int page, int limit, String sortBy, String sortDir, String name){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<Role> roles;
        if(name != null) roles = roleRepository.findByNameContaining(name, pageable);
        else roles = roleRepository.findAll(pageable);
        roles.forEach(role -> {
            List<Feature> features = featureRepository.findByRoles_Id(role.getId());
            role.setFeatures(features);
        });
        return roles.map(RoleResponse::new);
    }

    
}
