package com.example.library_management.repository;

import com.example.library_management.model.Comment;
import com.example.library_management.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    public Page<Comment>findByPostId(Long postId, Pageable pageable);
    public void deleteAllByPostIn(List<Post> posts);
    public void deleteAllByPost(Post post);
}
