package com.example.library_management.repository;

import com.example.library_management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByRole_NameAndIsDeletedFalse(String roleName);

    @EntityGraph(attributePaths = {"role", "role.features"})
    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    boolean existsByUsernameAndIsDeletedFalse(String username);
    Page<User> findByUsernameContainingAndIsDeletedFalse(String username, Pageable pageable);
    Page<User> findByFullNameContainingAndIsDeletedFalse(String fullName, Pageable pageable);
    Page<User> findByRole_NameContainingAndIsDeletedFalse(String role, Pageable pageable);

    boolean existsByEmailAndIsDeletedFalse(String email);
}