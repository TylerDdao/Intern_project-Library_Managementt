package com.example.library_management.repository;

import com.example.library_management.model.Genre;
import com.example.library_management.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, String> {
    Optional<Policy> findByKey(String key);
}
