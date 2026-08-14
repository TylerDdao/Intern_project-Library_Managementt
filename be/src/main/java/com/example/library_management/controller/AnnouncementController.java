package com.example.library_management.controller;

import com.example.library_management.dto.request.announcement.AnnouncementRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.announcement.AnnouncementResponse;
import com.example.library_management.service.announcement.CreateAnnouncementService;
import com.example.library_management.service.announcement.DeleteAnnouncementService;
import com.example.library_management.service.announcement.GetAnnouncementService;
import com.example.library_management.service.announcement.UpdateAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {
    @Autowired
    private CreateAnnouncementService createAnnouncementService;

    @Autowired
    private GetAnnouncementService getAnnouncementService;

    @Autowired
    private UpdateAnnouncementService updateAnnouncementService;

    @Autowired
    private DeleteAnnouncementService deleteAnnouncementService;


    @PreAuthorize("@securityService.hasAccess('ANNOUNCEMENTS_MANAGEMENT')")
    @PostMapping()
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
            @RequestBody AnnouncementRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(createAnnouncementService.createAnnouncement(request)));
    }

    @PreAuthorize("@securityService.hasAccess('ANNOUNCEMENTS_MANAGEMENT')")
    @PatchMapping()
    public ResponseEntity<ApiResponse<AnnouncementResponse>> updateAnnouncement(
            @RequestBody AnnouncementRequest request
    ){
        return ResponseEntity.ok(ApiResponse.success(updateAnnouncementService.updateAnnouncement(request)));
    }

    @PreAuthorize("@securityService.hasAccess('ANNOUNCEMENTS_MANAGEMENT')")
    @DeleteMapping()
    public ResponseEntity<ApiResponse<String>> deleteAnnouncement(
            @RequestParam Long id
    ){
        return ResponseEntity.ok(ApiResponse.success(deleteAnnouncementService.deleteAnnouncement(id)));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Page<AnnouncementResponse>>> getAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return ResponseEntity.ok(ApiResponse.success(getAnnouncementService.getAnnouncements(page, limit, sortBy, sortDir)));
    }
}
