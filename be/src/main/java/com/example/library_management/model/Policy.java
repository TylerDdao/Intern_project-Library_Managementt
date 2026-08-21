package com.example.library_management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "policies")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Policy extends BaseEntity {

    @Id
    @Column(name = "policy_key")
    private String key;
    @Column(name = "policy_value")
    private String value;
}
