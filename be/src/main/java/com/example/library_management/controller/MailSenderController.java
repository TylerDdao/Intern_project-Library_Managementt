package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.service.MailService;
import com.example.library_management.service.borrow.BorrowReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class MailSenderController {
    @Autowired
    MailService mailService;

    @Autowired
    BorrowReminderService borrowReminderService;

    @PreAuthorize("@securityService.hasAccess('GET_GENRE')")
    @GetMapping("/mail")
    public ResponseEntity<ApiResponse<String>> sendMail(
//            @RequestHeader(value = "Accept-Language", required = false) String lang
    ) {
        borrowReminderService.sendLateDueDateReminders();
        return ResponseEntity.ok(ApiResponse.success("Email sent successfully"));
    }

}
