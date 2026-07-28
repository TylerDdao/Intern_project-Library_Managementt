package com.example.library_management.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleRequest {
    private String name;
    private Long Id;

    public void setName(String name) {
        this.name = "ROLE_" + name.toUpperCase();
    }
}
