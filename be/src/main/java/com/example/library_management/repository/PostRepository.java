package com.example.library_management.repository;

import com.example.library_management.model.Post;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = """
    SELECT p FROM Post p
    LEFT JOIN p.book b
    WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(p.subject) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(p.createdBy) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
    """,
    countQuery = """
    SELECT COUNT(p) FROM Post p
    LEFT JOIN p.book b
    WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(p.subject) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(p.createdBy) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    Page<Post> findBySearchQuery(@Param("q") String query, Pageable pageable);

    Page<Post> findByCreatedBy(String createdBy, Pageable pageable);

    Page<Post> findByBookId(int bookId, Pageable pageable);
}
