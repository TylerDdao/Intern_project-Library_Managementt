package com.example.library_management.service.mail;

import com.example.library_management.exception.ApiException;
import com.example.library_management.model.User;
import com.example.library_management.repository.PolicyRepository;
import com.example.library_management.util.AuditLogger;
import com.example.library_management.util.Formatter;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserMailService {
    private final JavaMailSender mailSender;
    private final String html_header_multi_lang = """
    <div style="background:#2C5EAD;padding:28px 25px; text-align:center;">
        <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
            Library Management System
        </h1>
    </div>
    <!-- Language navigation -->
    <div style="padding:16px 25px;background:#F8FAFC;border-bottom:1px solid #E5E7EB;text-align:center;">
        <span style="font-size:12px;color:#6B7280;">Tiếng Việt</span>
        <span style="color:#CBD5E1; margin:0 10px;"> • </span>
        <span style="font-size:12px;color:#6B7280;">English</span>
        <span style="color:#CBD5E1; margin:0 10px;"> • </span>
        <span style="font-size:12px; color:#6B7280;">Français</span>
    </div>
    """;
    private final String html_single_lang = """
    <div style="background:#2C5EAD;padding:28px 25px; text-align:center;">
        <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
            Library Management System
        </h1>
    </div>
    """;
    private final String html_footer = """
    <div style="margin-top:20px; margin-bottom:20px;padding-top:18px;text-align:right;">
        <small>Library Management Team</small>
    </div>
    <div style="background:#F8FAFC; padding:18px 25px; text-align:center; border-top:1px solid #E5E7EB;">
        <p style="margin:0; font-size:11px; color:#9CA3AF; ">This is an automated message. Please do not reply to this email.</p>
    </div>
    """;
    @Value("${backend.url}")
    private String backEndUrl;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Formatter formatter;

    @Autowired
    private AuditLogger logger;

    public void sendWelcomeEmail(User user) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.welcome.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{user.getFullName()}, locale);
        String body = messageSource.getMessage("email.welcome.body", null, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);

        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div>%s</div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s</p>
                    <p>%s</p>
                </div>
                <div>%s</div>
            </div>
            """.formatted(html_single_lang, greeting, body, footer, html_footer);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            logger.log("Sent welcome email to {}", user.getEmail());
        }
        catch (Exception e){
            logger.log("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage());
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
                <div>%s</div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s</p>
                    <p>%s</p>
                </div>
                <div>%s</div>
            </div>
            """.formatted(html_single_lang, greeting, body, footer, html_footer);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            logger.log("Sent account deleted email to {}", user.getEmail());
        }
        catch (Exception e){
            logger.error("Failed to sent account deleted email to {}: {}", user.getEmail(), e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ISSUE", messageSource.getMessage("email.failed", null, locale));
        }
    }

    public void sendPasswordChangedEmail(User user)  {
        Locale locale = LocaleContextHolder.getLocale();
        String now = formatter.formatDateTime(LocalDateTime.now(), locale);
        String subject = messageSource.getMessage("email.password.changed.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{user.getFullName()}, locale);
        String bodyLabel = messageSource.getMessage("email.password.changed.body", new Object[]{now}, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);
        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div>%s</div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s</p>
                    <p>%s</p>
                </div>
                <div>%s</div>
            </div>
            """.formatted(html_single_lang, greeting, bodyLabel, footer, html_footer);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            logger.log("Sent password changed email to {}", user.getEmail());
        }
        catch (Exception e){
            logger.error("Failed to send password changed email to {}: {}", user.getEmail(), e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ISSUE", messageSource.getMessage("email.failed", null, locale));
        }
    }
}
