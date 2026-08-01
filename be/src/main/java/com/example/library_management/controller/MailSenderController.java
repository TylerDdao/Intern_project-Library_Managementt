package com.example.library_management.controller;

import com.example.library_management.dto.request.SmsRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.GenreResponse;
import com.example.library_management.service.MailService;
import com.example.library_management.service.SmsService;
import com.example.library_management.service.borrow.BorrowReminderService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

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
