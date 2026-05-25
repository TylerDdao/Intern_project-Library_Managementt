package com.example.library_management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 255)
    private String username;

    @OneToOne
    @Column(nullable = false)
    private Role role;

    @Column(name = "display_name")
    private String displayName;
}
