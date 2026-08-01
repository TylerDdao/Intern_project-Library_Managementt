package com.example.library_management.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Autowired
    private MessageSource messageSource;

    public String sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        return "Sent mail to " + to;
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = HTML
        mailSender.send(message);
    }

    public void sendLateBorrowReminder(String to, String user, String bookName, Long late, Locale locale) throws MessagingException {
        String subject = messageSource.getMessage("email.borrow.reminder.late.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting", new Object[]{user}, locale);
        String bodyLabel = messageSource.getMessage("email.borrow.reminder.due.late", new Object[]{late}, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);

        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p><strong>%s</strong> %s</p>
                    <p>%s</p>
                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
                </div>
            </div>
            """.formatted(greeting, bookName, bodyLabel, footer);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    public void sendBorrowDueReminder(String to, String user, String bookName, Locale locale) throws MessagingException {
        String subject = messageSource.getMessage("email.borrow.reminder.today.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting", new Object[]{user}, locale);
        String bodyLabel = messageSource.getMessage("email.borrow.reminder.due.today", null, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);

        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p><strong>%s</strong> %s</p>
                    <p>%s</p>
                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
                </div>
            </div>
            """.formatted(greeting, bookName, bodyLabel, footer);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    public void sentVerificationEmail(String to, String user, String code, Locale locale) throws MessagingException {
        String subject = messageSource.getMessage("email.verification.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting", new Object[]{user}, locale);
        String bodyLabel = messageSource.getMessage("email.verification.body", null, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);
        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s <strong>%s</strong></p>
                    <p>%s</p>
                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
                </div>
            </div>
            """.formatted(greeting, bodyLabel, code, footer);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true = HTML
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