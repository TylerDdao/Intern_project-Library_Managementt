package com.example.library_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    private String createdAt;
    private String createdBy;
    private String updatedAt;
    private String updatedBy;
    private boolean isActive;
    private boolean isDeleted;
}
