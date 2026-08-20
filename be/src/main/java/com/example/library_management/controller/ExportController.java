package com.example.library_management.controller;

import com.example.library_management.dto.request.book.BookRequest;
import com.example.library_management.dto.request.borrow.BorrowRequest;
import com.example.library_management.dto.request.user.UserRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.service.borrow.ExportBorrowService;
import com.example.library_management.service.log.ExportLogService;
import com.example.library_management.service.user.ExportBookService;
import com.example.library_management.service.user.ExportUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/export")
@Tag(name = "Export", description = "Export management endpoints" )
public class ExportController {
    @Autowired
    private ExportUserService exportUserService;
    @Autowired
    private ExportBookService exportBookService;
    @Autowired
    private ExportBorrowService exportBorrowService;
    @Autowired
    private ExportLogService exportLogService;
    @Autowired
    private MessageSource messageSource;

    @PreAuthorize("@securityService.hasAccess('EXPORT_BORROW')")
    @PostMapping("/borrows")
    @Operation(summary = "Export borrows", description = "Export all or selected borrows' information to Excel sheet, require 'EXPORT_BORROW' feature, default user shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
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
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
    })
    public ResponseEntity<InputStreamResource> exportBorrow(@RequestBody List<BorrowRequest> requests) {
        ByteArrayInputStream in = exportBorrowService.borrowsToExcel(requests);
        String filename = "borrows-export-" + LocalDate.now() + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @PreAuthorize("@securityService.hasAccess('EXPORT_USER')")
    @PostMapping("/users")
    @Operation(summary = "Export users", description = "Export all or selected users' information to Excel sheet, require 'EXPORT_USER' feature, default user shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
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
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
    })
    public ResponseEntity<InputStreamResource> exportUser(@RequestBody List<UserRequest> requests) {
        ByteArrayInputStream in = exportUserService.usersToExcel(requests);
        String filename = "users-export-" + LocalDate.now() + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @PreAuthorize("@securityService.hasAccess('EXPORT_BOOK')")
    @PostMapping("/books")
    @Operation(summary = "Export books", description = "Export all or selected books' information to Excel sheet, require 'EXPORT_BOOK' feature, default user shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
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
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
    })
    public ResponseEntity<InputStreamResource> exportBook(@RequestBody List<BookRequest> requests) {
        ByteArrayInputStream in = exportBookService.booksToExcel(requests);
        String filename = "books-export-" + LocalDate.now() + ".xlsx";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @PreAuthorize("@securityService.hasAccess('EXPORT_LOG')")
    @PostMapping("/log")
    @Operation(summary = "Export server log", description = "Export server log by date range to Excel sheet, require 'EXPORT_LOG' feature, default user shall not be granted this feature", security = @SecurityRequirement(name = "BearerAuth"))
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
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
    })
    public ResponseEntity<InputStreamResource> exportLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        LocalDate today = LocalDate.now();
        if (from.isAfter(today) || to.isAfter(today)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID-DATE-RANGE",
                    messageSource.getMessage("error.date.cannot.be.future", null, LocaleContextHolder.getLocale()));
        }
        if (from.isAfter(to)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID-DATE-RANGE",
                    messageSource.getMessage("error.invalid.date.range", null, LocaleContextHolder.getLocale()));
        }

        ByteArrayInputStream in = exportLogService.logsToExcel(from, to);
        HttpHeaders headers = new HttpHeaders();
        String filename = from.equals(to) ? "logs-" + from + ".xlsx" : "logs-" + from + "_to_" + to + ".xlsx";
        headers.add("Content-Disposition", "attachment; filename=" + filename);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

//    @PreAuthorize("@securityService.hasAccess('EXPORT_USER')")
//    @PostMapping("/users")
//    public ResponseEntity<ApiResponse<String>> exportUser(@RequestBody List<UserRequest> request) {
//        System.out.println(request);
//        return ResponseEntity.ok(ApiResponse.success("OK"));
//    }
}
