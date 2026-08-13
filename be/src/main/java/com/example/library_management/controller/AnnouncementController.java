package com.example.library_management.controller;

import com.example.library_management.dto.request.announcement.AnnouncementRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.announcement.AnnouncementResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {
//    @PreAuthorize("@sevurityService.hasAccess('ANNOUNCEMENTS_MANAGEMENT')")
//    @PostMapping()
//    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
//            @RequestBody AnnouncementRequest request
//            ){}
//
//    @PreAuthorize("@sevurityService.hasAccess('ANNOUNCEMENTS_MANAGEMENT')")
//    @PatchMapping()
//    public ResponseEntity<ApiResponse<AnnouncementResponse>> updateAnnouncement(
//            @RequestBody AnnouncementRequest request
//    ){}
//
//    @PreAuthorize("@sevurityService.hasAccess('ANNOUNCEMENTS_MANAGEMENT')")
//    @DeleteMapping()
//    public ResponseEntity<ApiResponse<String>> deleteAnnouncement(
//            @RequestParam Long id
//    ){}
//
//    @GetMapping()
//    public ResponseEntity<ApiResponse<Page<AnnouncementResponse>>> getAnnouncement(
//    ){}
}
