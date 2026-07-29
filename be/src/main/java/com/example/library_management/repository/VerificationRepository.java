package com.example.library_management.repository;

import com.example.library_management.model.EmailVerification;
import com.example.library_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<EmailVerification, Long> {
    boolean existsByEmailAndCodeAndVerifiedFalseAndExpiresAtAfter(String email, String code, LocalDateTime now);

    boolean existsByEmailAndVerifiedFalseAndExpiresAtAfter(String email, LocalDateTime now);

    long deleteByExpiresAtBeforeAndVerifiedFalse(LocalDateTime dateTime);

    Optional<EmailVerification> findTopByEmailOrderByCreatedAtDesc(String email);
}
