package com.example.library_management.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "like_count", nullable = false)
    private long likeCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
