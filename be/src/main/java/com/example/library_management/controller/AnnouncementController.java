package com.example.library_management.controller;

import com.example.library_management.dto.request.announcement.AnnouncementRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.announcement.AnnouncementResponse;
import com.example.library_management.service.announcement.CreateAnnouncementService;
import com.example.library_management.service.announcement.DeleteAnnouncementService;
import com.example.library_management.service.announcement.GetAnnouncementService;
import com.example.library_management.service.announcement.UpdateAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Announcements", description = "Announcements management endpoints")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "BearerAuth")
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
    @Operation(summary = "Create announcement", description = "Create a new announcement, require 'ANNOUNCEMENT_MANAGEMENT' feature, default users shall not be granted this feature")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
    })
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
            @RequestBody AnnouncementRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(createAnnouncementService.createAnnouncement(request)));
    }

    @PreAuthorize("@securityService.hasAccess('ANNOUNCEMENTS_MANAGEMENT')")
    @PatchMapping()
    @Operation(summary = "Update announcement", description = "Update an announcement's status between 'Active' and 'Inactive', require 'ANNOUNCEMENT_MANAGEMENT' feature, default users shall not be granted this feature")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Announcement not found",
                                            description = "Announcement can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "ANNOUNCEMENT-NOT-FOUND",
                                                    "message": "Announcement not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<AnnouncementResponse>> updateAnnouncement(
            @RequestBody AnnouncementRequest request
    ){
        return ResponseEntity.ok(ApiResponse.success(updateAnnouncementService.updateAnnouncement(request)));
    }

    @PreAuthorize("@securityService.hasAccess('ANNOUNCEMENTS_MANAGEMENT')")
    @DeleteMapping()
    @Operation(summary = "Delete announcement", description = "Delete an announcement, require 'ANNOUNCEMENT_MANAGEMENT' feature, default users shall not be granted this feature")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Announcement not found",
                                            description = "Announcement can't be found by its ID",
                                            value = """
                                                    {
                                                    "code": "ANNOUNCEMENT-NOT-FOUND",
                                                    "message": "Announcement not found",
                                                    "data": null,
                                                    "timestamp": "2026-08-19T10:00:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    public ResponseEntity<ApiResponse<String>> deleteAnnouncement(
            @RequestParam Long id
    ){
        return ResponseEntity.ok(ApiResponse.success(deleteAnnouncementService.deleteAnnouncement(id)));
    }

    @GetMapping()
    @Operation(summary = "Get announcements", description = "Get all announcements regardless of status")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<Page<AnnouncementResponse>>> getAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return ResponseEntity.ok(ApiResponse.success(getAnnouncementService.getAnnouncements(page, limit, sortBy, sortDir)));
    }
}
