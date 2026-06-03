package com.example.library_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookRequest {
    private String title;
    private String author;
    private List<String> genres;
}
