package com.example.library_management.repository;

import com.example.library_management.model.Genre;
import com.example.library_management.model.Post;
import com.example.library_management.model.PostLike;
import com.example.library_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user = :user")
    public List<Long> findLikedPostIdsByUser(@Param("user") User user);

    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user.username = :username")
    public List<Long> findLikedPostIdsByUserUsername(@Param("username") String username);

    public Optional<PostLike> findByPostAndUser(Post post, User user);

    public Optional<PostLike> findByPostIdAndUserUsername(Long postId, String username);

    public void deleteAllByPost_IdIn(List<Long> postIds);

    public void deleteAllByPost(Post post);
}
