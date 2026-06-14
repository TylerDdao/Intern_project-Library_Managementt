package com.example.library_management.repository;

import com.example.library_management.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    public Page<Comment>findByPostId(Long postId, Pageable pageable);
}
