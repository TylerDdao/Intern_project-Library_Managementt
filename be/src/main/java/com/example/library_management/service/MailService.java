package com.example.library_management.service;

import com.example.library_management.exception.ApiException;
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

    public void sendHtmlEmail(String to, String user, String subject, String body, Locale locale) throws MessagingException {
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{user}, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);

        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s</p>
                    <p>%s</p>
                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
                </div>
            </div>
            """.formatted(greeting, body, footer);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    public void sendWelcomeEmail(User user) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.welcome.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{user.getFullName()}, locale);
        String body = messageSource.getMessage("email.welcome.body", null, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);

        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s</p>
                    <p>%s</p>
                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
                </div>
            </div>
            """.formatted(greeting, body, footer);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        }
        catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ISSUE", messageSource.getMessage("email.failed", null, locale));
        }
    }

    public void sendAccountDeletedEmail(User user) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.deleted.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{user.getFullName()}, locale);
        String body = messageSource.getMessage("email.deleted.body", null, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);

        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s</p>
                    <p>%s</p>
                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
                </div>
            </div>
            """.formatted(greeting, body, footer);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        }
        catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ISSUE", messageSource.getMessage("email.failed", null, locale));
        }
    }

    public void sendResetPasswordEmail(String to, String resetLink, Integer activeDuration) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.reset.password.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting", null, locale);
        String bodyLabel = messageSource.getMessage("email.reset.password.body", new Object[]{activeDuration}, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);
        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s</p>
                    <strong>%s</strong>
                    <p>%s</p>
                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
                </div>
            </div>
            """.formatted(greeting, bodyLabel, resetLink, footer);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    public void sentVerificationEmail(String to, String user, String code, Integer activeDuration) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.verification.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting", null, locale);
        String bodyLabel = messageSource.getMessage("email.verification.body", new Object[]{code, activeDuration}, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);
        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s</p>
                    <p>%s</p>
                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
                </div>
            </div>
            """.formatted(greeting, bodyLabel, footer);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    private String buildHtmlTemplate(String greeting, String bodyLabel, String code, String footer) {
        return """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s <strong>%s</strong></p>
                    <p style="text-align:right;margin:0;"><small>%s</small></p>
                </div>
            </div>
            """.formatted(greeting, bodyLabel, code, footer);
    }
}