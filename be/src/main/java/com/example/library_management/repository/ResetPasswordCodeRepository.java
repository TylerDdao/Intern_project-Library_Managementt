package com.example.library_management.repository;

import com.example.library_management.model.EmailVerification;
import com.example.library_management.model.ResetPasswordCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ResetPasswordCodeRepository extends JpaRepository<ResetPasswordCode, Long> {
    boolean existsByUser_EmailAndCodeAndIsResetFalseAndExpiresAtAfter(String email, String code, LocalDateTime now);
    boolean existsByUser_EmailAndIsResetFalseAndExpiresAtAfter(String email, LocalDateTime now);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);

    void deleteByUser_Email(String email);

    Optional<ResetPasswordCode> findTopByUser_EmailOrderByCreatedAtDesc(String email);
}
