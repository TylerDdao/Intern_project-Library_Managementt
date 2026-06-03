package com.example.library_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleRequest {
    private String name;

    public void setName(String name) {
        this.name = "ROLE_" + name.toUpperCase();
    }
}
