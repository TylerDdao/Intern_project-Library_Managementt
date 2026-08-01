package com.example.library_management.repository;

import com.example.library_management.model.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    public List<Borrow> findByUserId(int userId);
    public Optional<Borrow> findByUserIdAndBookIdAndIsActive(Long userId, Long bookId, Boolean isActive);
    Optional<Borrow> findByUserUsernameAndBookIdAndIsActive(String username, Long bookId, Boolean isActive);

    public List<Borrow> findByDueDateBetweenAndIsActiveTrue(LocalDateTime start, LocalDateTime end);
    public List<Borrow> findByDueDateLessThanAndIsActiveTrue(LocalDateTime date);

    public Page<Borrow> findByDueDateBetweenAndIsActiveTrue(LocalDateTime start, LocalDateTime end, Pageable pageable);
    public Page<Borrow> findByDueDateLessThanAndIsActiveTrue(LocalDateTime date, Pageable pageable);
    public Page<Borrow> findByDueDateGreaterThanAndIsActiveTrue(LocalDateTime date, Pageable pageable);
    public Page<Borrow> findByIsActive(boolean isActive, Pageable pageable);

    Page<Borrow> findByUserUsernameAndDueDateLessThanAndIsActiveTrue(String username, LocalDateTime date, Pageable pageable);
    Page<Borrow> findByUserUsernameAndDueDateBetweenAndIsActiveTrue(String username, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Borrow> findByUserUsernameAndDueDateGreaterThanAndIsActiveTrue(String username, LocalDateTime date, Pageable pageable);
    Page<Borrow> findByUserUsernameAndIsActive(String username, boolean isActive, Pageable pageable);
    Page<Borrow> findByIsActiveAndBookId(boolean isActive, Long bookId, Pageable pageable);

    Page<Borrow> findByUserUsername(String username, Pageable pageable);

    @Query("""
        SELECT g.name, COUNT(br) FROM Borrow br
        JOIN br.book b
        JOIN b.genres g
        GROUP BY g.name
        """)
    List<Object[]> countBorrowsByGenre();

    @Query(value = """
    SELECT b FROM Borrow b
    LEFT JOIN b.user u
    LEFT JOIN b.book bk
    WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(bk.title) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(bk.author) LIKE LOWER(CONCAT('%', :q, '%'))
    """,
            countQuery = """
    SELECT COUNT(b) FROM Borrow b
    LEFT JOIN b.user u
    LEFT JOIN b.book bk
    WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(bk.title) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(bk.author) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    Page<Borrow> findBySearchQuery(@Param("q") String query, Pageable pageable);

    @Query(value = """
    SELECT b FROM Borrow b
    LEFT JOIN b.user u
    LEFT JOIN b.book bk
    WHERE u.username = :username
      AND (
        LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(bk.title) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(bk.author) LIKE LOWER(CONCAT('%', :q, '%'))
      )
    """,
            countQuery = """
    SELECT COUNT(b) FROM Borrow b
    LEFT JOIN b.user u
    LEFT JOIN b.book bk
    WHERE u.username = :username
      AND (
        LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(bk.title) LIKE LOWER(CONCAT('%', :q, '%'))
        OR LOWER(bk.author) LIKE LOWER(CONCAT('%', :q, '%'))
      )
    """)
    Page<Borrow> findBySearchQueryAndUsername(@Param("q") String query, @Param("username") String username, Pageable pageable);

    Page<Borrow> findByIsActiveAndUserUsername(boolean isActive, String username, Pageable pageable);
}
