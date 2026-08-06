package com.example.library_management.repository;

import com.example.library_management.model.Book;
import com.example.library_management.model.Genre;
import com.example.library_management.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title);
    Page<Book> findByCopies(int copies, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdForUpdate(Long id);

    // Search across title, author, genre name
    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN b.genres g
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(g.name) LIKE LOWER(CONCAT('%', :q, '%'))
        """)
    Page<Book> findBySearchQuery(@Param("q") String query, Pageable pageable);

    // Filter by genre names (filterBy = ["Fiction", "Science"])
    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN b.genres g
        WHERE g.name IN :filters
        """)
    Page<Book> findByFilters(@Param("filters") List<String> filters, Pageable pageable);

    // Combined: search + filter
    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN b.genres g
        WHERE g.name IN :filters
          AND (
            LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(g.name) LIKE LOWER(CONCAT('%', :q, '%'))
          )
        """)
    Page<Book> findBySearchQueryAndFilters(@Param("q") String query,
                                           @Param("filters") List<String> filters,
                                           Pageable pageable);

    @Query("""
    SELECT b FROM Book b
    ORDER BY createdAt DESC
    """)
    Page<Book> findMostRecentBooks(Pageable pageable);

    @Query("""
    SELECT b FROM Book b
    ORDER BY (SELECT COUNT(p) FROM Post p WHERE p.book = b) DESC
    """)
    Page<Book> findMostPostsBooks(Pageable pageable);

    @Query("""
    SELECT b FROM Book b
    ORDER BY (SELECT COUNT(br) FROM Borrow br WHERE br.book = b) DESC
    """)
    Page<Book> findMostBorrowedBooks(Pageable pageable);

    @Query("""
    SELECT DISTINCT b FROM Book b
    JOIN b.genres g
    JOIN Borrow br ON br.book = b
    WHERE g.name = :genre
    """)
    Page<Book> findBorrowedBooksByGenre(@Param("genre") String genre, Pageable pageable);

    @Query("""
    SELECT g.name, COUNT(b) FROM Book b
    JOIN b.genres g
    GROUP BY g.name""")
    List<Object[]> countBooksByGenre();

    List<Book> findByGenresContaining(Genre genre);
}

