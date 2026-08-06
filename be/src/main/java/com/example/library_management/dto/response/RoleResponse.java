package com.example.library_management.dto.response;

import com.example.library_management.model.Feature;
import com.example.library_management.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class RoleResponse {
    private long id;
    private String name;
    private Boolean isDefault = false;
    private List<FeatureResponse> features = new ArrayList<>();

    public RoleResponse(Role role){
        this.id = role.getId();
        this.name = role.getName();
        this.isDefault = role.getIsDefault();
        if (role.getFeatures() != null) {
            this.features = role.getFeatures().stream().map(FeatureResponse::new).collect(Collectors.toList());
        }
    }
}
