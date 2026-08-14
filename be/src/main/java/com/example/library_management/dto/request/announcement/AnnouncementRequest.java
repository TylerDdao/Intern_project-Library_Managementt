package com.example.library_management.dto.request.announcement;

import com.example.library_management.model.AnnouncementType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementRequest {
    private Long id = null;

    private AnnouncementType type = AnnouncementType.INFO;
    private String subjectVi;
    private String contentVi;

    private String subjectEn = null;
    private String contentEn = null;

    private String subjectFr = null;
    private String contentFr = null;

    private String link = null;
    private String linkTextVi;
    private String linkTextEn = null;
    private String linkTextFr = null;
    private Boolean isActive = true;

    private List<String> locations = null;
}