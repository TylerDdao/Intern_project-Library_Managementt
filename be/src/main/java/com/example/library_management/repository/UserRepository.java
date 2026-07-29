package com.example.library_management.repository;

import com.example.library_management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"role", "role.features"})
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByFullNameContaining(String fullName, Pageable pageable);
    Page<User> findByRole_NameContaining(String role, Pageable pageable);

    boolean existsByEmail(String email);
}