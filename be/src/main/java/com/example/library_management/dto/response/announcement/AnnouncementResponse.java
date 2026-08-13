package com.example.library_management.dto.response.announcement;

import com.example.library_management.model.AnnouncementType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementResponse {
    private Long id;

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
    private boolean isActive = true;

    private List<String> locations = null;
}
