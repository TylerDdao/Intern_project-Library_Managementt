package com.example.library_management.repository;

import com.example.library_management.model.Comment;
import com.example.library_management.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    public Page<Comment>findByPostId(Long postId, Pageable pageable);
    public void deleteAllByPostIn(List<Post> posts);
}
