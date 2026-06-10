package com.example.library_management.repository;

import com.example.library_management.model.Borrow;
import com.example.library_management.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    public Page<Borrow> findByUserId(Long userId, Pageable pageable);
    public Optional<Borrow> findByUserIdAndBookId(Long userId, Long bookId);
    public Page<Borrow> findByBookId(Long bookId, Pageable pageable);
    public List<Borrow> findByDueDateBetween(LocalDateTime start, LocalDateTime end);
}
