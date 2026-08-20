package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.exception.AuthException;
import com.example.library_management.model.Borrow;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.service.mail.BorrowMailService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class TestController {

    @Autowired
    BorrowMailService borrowMailService;

    @Autowired
    BorrowRepository borrowRepository;

    @Autowired
    MessageSource messageSource;

    @GetMapping("test")
    public ResponseEntity<ApiResponse<String>> Test(){
//        User user = new User();
//        user.setFullName("Tyler");
//        user.setEmail("baonam6a3@gmail.com");
//        Long borrowId = Long.parseLong("3");
//        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(()->new RuntimeException("ERROR"));
//        borrowMailService.sendBorrowReturned(borrow);
//        return ResponseEntity.ok(ApiResponse.success("OK"));

        throw new AuthException(messageSource.getMessage("error.captcha.failed", null, LocaleContextHolder.getLocale()));
    }
}
