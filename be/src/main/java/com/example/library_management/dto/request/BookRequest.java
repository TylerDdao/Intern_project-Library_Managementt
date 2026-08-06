package com.example.library_management.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookRequest {
    private Long id = null;
    private String title;
    private String author;
    private List<String> genres;
    private Integer copies;
    private String coverUrl;
}
