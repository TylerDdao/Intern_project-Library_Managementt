package com.example.library_management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

public enum Role {
    ROLE_USER,
    ROLE_MANAGER,
    ROLE_ADMIN
}