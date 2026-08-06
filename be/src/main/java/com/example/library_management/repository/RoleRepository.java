package com.example.library_management.repository;

import com.example.library_management.model.Role;
import com.example.library_management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    Page<Role> findByNameContaining(String name, Pageable pageable);
    Optional<Role> findByIsDefaultIsTrue();
    boolean existsByNameAndIsDefaultTrue(String name);
}