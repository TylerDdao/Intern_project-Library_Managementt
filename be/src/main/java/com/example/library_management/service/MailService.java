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

    public void sendLateBorrowReminder(User user, String bookName, Long late) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.borrow.reminder.late.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{user.getFullName()}, locale);
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

    public void sendBorrowDueReminder(User user, String bookName) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.borrow.reminder.today.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{user.getFullName()}, locale);
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

    public void sendBorrowCreatedEmail(Borrow borrow) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.borrow.created.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, locale);
        String body = messageSource.getMessage("email.borrow.created.body", null, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);

        String title = borrow.getBook().getTitle();
        String bookCoverFile = borrow.getBook().getCoverUrl() != null
                ? borrow.getBook().getCoverUrl()
                : "default.jpg";
        String author = borrow.getBook().getAuthor();
        String borrowOn = messageSource.getMessage("email.borrow.at", new Object[]{formatter.formatDateTime(borrow.getCreatedAt())}, LocaleContextHolder.getLocale());
        String returnRemind = messageSource.getMessage("email.borrow.created.return.remind", null, LocaleContextHolder.getLocale());
        String dueOn = formatter.formatDateTime(borrow.getDueDate());
        Policy penalty;
        String penaltyFee;
        if(policyRepository.findByKey("late_penalty_per_day").isPresent()){
            penalty = policyRepository.findByKey("late_penalty_per_day").orElse(new Policy());
            penaltyFee = messageSource.getMessage("email.borrow.created.penalty.fee", new Object[]{formatter.formatVND(Long.parseLong(penalty.getValue()))}, locale);
        }
        else {
            penaltyFee = messageSource.getMessage("email.borrow.created.penalty.fee", new Object[]{messageSource.getMessage("error.Unknown", null, LocaleContextHolder.getLocale())}, locale);
        }
        String penaltyFeeAnnounce = messageSource.getMessage("email.borrow.created.penalty.announcement", null, locale);


        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s</p>
                    <div style="display: flex;">
                        <div style="width: 100px; margin-right: 10px;">
                          <img src="%s/book-covers/%s" style="width: 100%%; display: block;"/>
                      </div>
                        <div>
                            <div style="font-weight: bold;">%s</div>
                            <div>%s</div>
                            <div>%s</div>
                            <div style="text-decoration: underline;">%s: <strong>%s</strong></div>
                        </div>
                    </div>
                    <div>%s</div>
                    <div>%s</div>
                    <div>%s</div>
                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
                </div>
            </div>
            """.formatted(greeting, body, backEndUrl, bookCoverFile, title, author, borrowOn, returnRemind, dueOn, penaltyFee, penaltyFeeAnnounce, footer);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(borrow.getUser().getEmail());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        }
        catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ISSUE", messageSource.getMessage("email.failed", null, locale));
        }
    }

    public void sendBorrowReturned(Borrow borrow) {
//        Locale locale = LocaleContextHolder.getLocale();
//        String subject = messageSource.getMessage("email.borrow.returned.subject", null, locale);
//        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, locale);
//        String body = messageSource.getMessage("email.borrow.returned.body", new Object[]{borrow.getBook().getTitle(), formatter.formatDateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))}, locale);
//        String footer = messageSource.getMessage("email.footer", null, locale);
//
//
//        String html = """
//            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
//                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
//                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
//                </div>
//                <div style="padding:20px;color:#25343F;">
//                    <h2>%s</h2>
//                    <p>%s</p>
//                    <div>%s</div>
//                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
//                </div>
//            </div>
//            """.formatted(greeting, body, footer);

        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.borrow.returned.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, locale);
        String body = messageSource.getMessage("email.borrow.returned.body", new Object[]{formatter.formatDateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))}, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);

        String title = borrow.getBook().getTitle();
        String bookCoverFile = borrow.getBook().getCoverUrl() != null
                ? borrow.getBook().getCoverUrl()
                : "default.jpg";
        String author = borrow.getBook().getAuthor();


        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background-color:#2C5EAD;padding:20px;border-radius:10px 10px 0 0;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;">Library Management System</h1>
                </div>
                <div style="padding:20px;color:#25343F;">
                    <h2>%s</h2>
                    <p>%s</p>
                    <div style="display: flex;">
                        <div style="width: 100px; margin-right: 10px;">
                          <img src="%s/book-covers/%s" style="width: 100%%; display: block;"/>
                      </div>
                        <div>
                            <div style="font-weight: bold;">%s</div>
                            <div>%s</div>
                        </div>
                    </div>
                    <div>%s</div>
                    <p style="text-align:right;margin:0;"><small>Library Management Team</small></p>
                </div>
            </div>
            """.formatted(greeting, body, backEndUrl, bookCoverFile, title, author, footer);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(borrow.getUser().getEmail());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        }
        catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ISSUE", messageSource.getMessage("email.failed", null, locale));
        }
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