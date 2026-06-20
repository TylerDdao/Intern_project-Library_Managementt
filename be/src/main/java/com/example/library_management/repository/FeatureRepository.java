package com.example.library_management.repository;

import com.example.library_management.model.Feature;
import com.example.library_management.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long>{
    List<Feature> findByRoles_Id(long roleId);
    Page<Feature> findByRoles_Name(String role, Pageable pageable);
    Optional<Feature> findByName(String name);
}
