package com.example.library_management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "posts")
@EqualsAndHashCode(callSuper = true)
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @Formula("(SELECT COUNT(pl.id) FROM post_likes pl WHERE pl.post_id = id)")
    private long likeCount;

    @Formula("(SELECT COUNT(c.id) FROM comments c WHERE c.post_id = id)")
    private long commentCount;
}
