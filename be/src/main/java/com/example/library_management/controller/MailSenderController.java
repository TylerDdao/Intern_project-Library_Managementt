package com.example.library_management.controller;

import com.example.library_management.dto.request.SmsRequest;
import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.dto.response.GenreResponse;
import com.example.library_management.service.MailService;
import com.example.library_management.service.SmsService;
import com.example.library_management.service.borrow.BorrowReminderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailSenderController {
    @Autowired
    MailService mailService;

    @Autowired
    SmsService smsService;

    @Autowired
    BorrowReminderService borrowReminderService;

    @PreAuthorize("@securityService.hasAccess('GET_GENRE')")
    @GetMapping("/mail")
    public ResponseEntity<ApiResponse<String>> sendMail(){
        return ResponseEntity.ok(
                ApiResponse.success(borrowReminderService.test())
        );
    }

    @PreAuthorize("@securityService.hasAccess('GET_GENRE')")
    @GetMapping("/sms")
    public ResponseEntity<ApiResponse<String>> sendTestSms(@Valid @RequestBody SmsRequest request) {
        smsService.sendSms(request.getToNumber(), request.getMessage());
        return ResponseEntity.ok(ApiResponse.success("SMS sent to " + request.getToNumber()));
    }


}
