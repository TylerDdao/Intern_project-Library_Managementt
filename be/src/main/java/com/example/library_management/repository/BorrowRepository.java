package com.example.library_management.repository;

import com.example.library_management.model.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    public List<Borrow> findByUserId(int userId);

    public Page<Borrow> findByUserId(Long userId, Pageable pageable);
    public Optional<Borrow> findByUserIdAndBookId(Long userId, Long bookId);
    public Page<Borrow> findByBookId(Long bookId, Pageable pageable);
    public List<Borrow> findByDueDateBetweenAndIsActiveTrue(LocalDateTime start, LocalDateTime end);
    public Page<Borrow> findByDueDateBetweenAndIsActiveTrue(LocalDateTime start, LocalDateTime end, Pageable pageable);
    public Page<Borrow> findByDueDateLessThanAndIsActiveTrue(LocalDateTime date, Pageable pageable);
    public Page<Borrow> findByDueDateGreaterThanAndIsActiveTrue(LocalDateTime date, Pageable pageable);
    public Page<Borrow> findByIsActive(boolean isActive, Pageable pageable);


    @Query("""
        SELECT g.name, COUNT(br) FROM Borrow br
        JOIN br.book b
        JOIN b.genres g
        GROUP BY g.name
        """)
    List<Object[]> countBorrowsByGenre();
}
