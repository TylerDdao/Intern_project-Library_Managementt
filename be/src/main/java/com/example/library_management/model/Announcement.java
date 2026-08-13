package com.example.library_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "announcements")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Announcement extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private AnnouncementType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String subjectVi;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contentVi;

    @Column(columnDefinition = "TEXT")
    private String subjectEn;

    @Column(columnDefinition = "TEXT")
    private String contentEn;

    @Column(columnDefinition = "TEXT")
    private String subjectFr;

    @Column(columnDefinition = "TEXT")
    private String contentFr;

    @Column(columnDefinition = "TEXT")
    private String link;

    @Column(columnDefinition = "TEXT")
    private String linkTextVi;

    @Column(columnDefinition = "TEXT")
    private String linkTextEn;

    @Column(columnDefinition = "TEXT")
    private String linkTextFr;

    @Column(nullable = false)
    private boolean isActive;

    @ElementCollection
    @CollectionTable(
            name = "announcement_locations",
            joinColumns = @JoinColumn(name = "announcement_id")
    )
    @Column(name = "location")
    @Builder.Default
    private List<String> locations = new ArrayList<>();
}