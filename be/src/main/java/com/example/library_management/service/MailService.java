package com.example.library_management.service;

import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Book;
import com.example.library_management.model.Borrow;
import com.example.library_management.model.Policy;
import com.example.library_management.model.User;
import com.example.library_management.repository.PolicyRepository;
import com.example.library_management.util.Formatter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tools.jackson.core.internal.shaded.fdp.JavaFloatParser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${backend.url}")
    private String backEndUrl;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private Formatter formatter;

//    public void sendHtmlEmail(String to, String user, String subject, String body, Locale locale) throws MessagingException {
//        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{user}, locale);
//        String footer = messageSource.getMessage("email.footer", null, locale);
//
//        String html = """
//            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
//                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
//                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
//                </div>
//                <div style="padding:20px;color:#25343F;">
//                    <h2>%s</h2>
//                    <p>%s</p>
//                    <p>%s</p>
//                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
//                </div>
//            </div>
//            """.formatted(greeting, body, footer);
//
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(html, true);
//        mailSender.send(message);
//    }


//    private String buildHtmlTemplate(String greeting, String bodyLabel, String code, String footer) {
//        return """
//            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
//                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
//                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
//                </div>
//                <div style="padding:20px;color:#25343F;">
//                    <h2>%s</h2>
//                    <p>%s <strong>%s</strong></p>
//                    <p style="text-align:right;margin:0;"><small>%s</small></p>
//                </div>
//            </div>
//            """.formatted(greeting, bodyLabel, code, footer);
//    }
}