package com.example.library_management.controller;

import com.example.library_management.dto.response.ApiResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Borrow;
import com.example.library_management.model.User;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.service.MailService;
import com.example.library_management.service.mail.BorrowMailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.standard.expression.MessageExpression;

@RestController
public class TestController {

    @Autowired
    BorrowMailService borrowMailService;

    @Autowired
    BorrowRepository borrowRepository;

    @Autowired
    BorrowRepository borrowRepository;

    @GetMapping("test")
    public ResponseEntity<ApiResponse<String>> Test(){
//        User user = new User();
//        user.setFullName("Tyler");
//        user.setEmail("baonam6a3@gmail.com");
        Long borrowId = Long.parseLong("3");
        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(()->new RuntimeException("ERROR"));
        borrowMailService.sendLateBorrowReminder(borrow);
        return ResponseEntity.ok(ApiResponse.success("OK"));
    }
}
