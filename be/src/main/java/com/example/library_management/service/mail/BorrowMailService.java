package com.example.library_management.service.mail;

import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Book;
import com.example.library_management.model.Borrow;
import com.example.library_management.model.Policy;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BorrowMailService {
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
    private PolicyRepository policyRepository;

    @Autowired
    private Formatter formatter;

    @Autowired
    private AuditLogger logger;

    private String buildHtmlBorrowCard(Borrow borrow, Locale locale){
        String borrowString = messageSource.getMessage("borrow.borrow", null, locale);
        String bookCoverFile = borrow.getBook().getCoverUrl() != null
                ? borrow.getBook().getCoverUrl()
                : "default.jpg";
        String bookCoverUrl = backEndUrl + "/book-covers/" + bookCoverFile;
        String borrowedAt = messageSource.getMessage("email.borrow.at", new Object[]{formatter.formatDateTime(borrow.getCreatedAt(), locale)}, locale);
        Optional<Policy> penaltyPolicy = policyRepository.findByKey("late_penalty_per_day");
        String penaltyRate;
        if (penaltyPolicy.isPresent()) {
            penaltyRate = messageSource.getMessage("email.borrow.penalty.fee", new Object[]{formatter.formatVND(Long.parseLong(penaltyPolicy.get().getValue()))}, locale);
        } else {
            penaltyRate = messageSource.getMessage("error.Unknown", null, locale);
        }
        return """
                <div style="background:#F3F7F2;border-left:4px solid #5C8D5A;padding:16px 18px;margin:20px 0;border-radius:6px;">
                    <div style="font-size:12px;color:#6B7280;text-transform:uppercase;letter-spacing:.5px;margin-bottom:5px;">%s</div>
                    <div style="display: flex;">
                        <img src="%s" style=" width:100px; object-fit:cover; border-radius:5px; display:block;"/>
                        <div style="margin-left: 10px;">
                            <div style="font-size:17px;font-weight:bold;color:#25343F; ">
                                %s
                            </div>
                            <div style="font-size:17px;color:#25343F;">
                                %s
                            </div>
                            <div style="font-size:13px; line-height:1.8; color:#4B5563;">
                                <div>%s</div>
                                <div>%s</div>
                            </div>
                        </div>
                    </div>
                </div>
                """.formatted(borrowString, bookCoverUrl, borrow.getBook().getTitle(), borrow.getBook().getAuthor(), borrowedAt, penaltyRate);
    }

    private String buildHtmlLateBorrowCard(Borrow borrow, Locale locale){
        String lateBorrow = messageSource.getMessage("borrow.borrow.late", null, locale);
        String bookCoverFile = borrow.getBook().getCoverUrl() != null
                ? borrow.getBook().getCoverUrl()
                : "default.jpg";
        String bookCoverUrl = backEndUrl + "/book-covers/" + bookCoverFile;
        String borrowedAt_vi = messageSource.getMessage("email.borrow.at", new Object[]{formatter.formatDateTime(borrow.getCreatedAt(), locale)}, locale);
        Long late = formatter.calculateDaysBetween(
                borrow.getDueDate().toLocalDate(),
                LocalDate.now()
        );
        Optional<Policy> penaltyPolicy = policyRepository.findByKey("late_penalty_per_day");
        String penaltyFee_vi = messageSource.getMessage("email.borrow.late.penalty", new Object[]{formatter.formatVND(borrow.getPenalty())}, locale);
        String penaltyRate_vi;
        if (penaltyPolicy.isPresent()) {
            penaltyRate_vi = messageSource.getMessage("email.borrow.penalty.fee", new Object[]{formatter.formatVND(Long.parseLong(penaltyPolicy.get().getValue()))}, locale);
        } else {
            penaltyRate_vi = messageSource.getMessage("error.Unknown", null, locale);
        }
        return """
                <div style="background:#FEF2F2; border-left:4px solid #DC2626; padding:16px 18px; margin:20px 0; border-radius:6px;">
                <!-- Label -->
                <div style="font-size:12px; color:#B91C1C; text-transform:uppercase; letter-spacing:.5px; margin-bottom:12px; font-weight:bold;">%s</div>
                <!-- Book information -->
                <div style=" display:flex; align-items:flex-start;">
                    <img src="%s" style=" width:100px; object-fit:cover; border-radius:5px; display:block;"/>
                    <div style=" margin-left:15px; flex:1;">
                        <!-- Title -->
                        <div style=" font-size:18px; font-weight:bold; color:#991B1B; margin-bottom:4px;">%s</div>
                        <!-- Author -->
                        <div style=" font-size:14px; color:#7F1D1D; margin-bottom:14px;">%s</div>
                        <!-- Borrow details -->
                        <div style="font-size:13px; line-height:1.8; color:#4B5563;">
                            <div>%s</div>
                            <div>Late: <strong>%s days</strong></div>
                            <div>%s</div>
                            <div>%s</div>
                        </div>
                    </div>
                </div>
                <!-- Warning -->
                <div style=" margin-top:18px; padding-top:14px; border-top:1px solid #FECACA; font-size:13px; color:#991B1B;">Vui lòng trả sách sớm nhất có thể để tránh phát sinh thêm tiền phạt.</div>
            </div>
                """.formatted(lateBorrow, bookCoverUrl, borrow.getBook().getTitle(), borrow.getBook().getAuthor(), borrowedAt_vi, late, penaltyFee_vi, penaltyRate_vi);
    }

    public void sendLateBorrowReminder(Borrow borrow) {
        Long late = formatter.calculateDaysBetween(borrow.getDueDate().toLocalDate(), LocalDate.now());
        Locale vi = Locale.of("vi", "VN");
        Locale en = Locale.US;
        Locale fr = Locale.CANADA_FRENCH;
        String subject_vi = messageSource.getMessage("email.borrow.reminder.late.subject", null, vi);
        String greeting_vi = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, vi);
        String bodyLabel_vi = messageSource.getMessage("email.borrow.reminder.due.late", new Object[]{late}, vi);
        String footer_vi = messageSource.getMessage("email.footer", null, vi);

        String html_vi = """
        <div style="padding:28px 30px;color:#25343F;">
            <!-- Section label -->
            <div style="font-size:13px; color:#B91C1C; text-transform:uppercase; letter-spacing:1px; margin-bottom:8px; font-weight:bold;">Sách quá hạn</div>

            <!-- Greeting -->
            <h2 style="margin:0 0 20px; color:#25343F; font-size:22px;">%s</h2>

            <!-- Reminder -->
            <p style="font-size:15px; line-height:1.7; margin:0 0 20px;"><strong>%s</strong> %s</p>

            <!-- Late book card -->
            <div>%s</div>
            <!-- Footer text -->
            <p style=" font-size:14px; line-height:1.7; color:#59636E;margin-top:20px; margin-bottom:20px;">%s</p>
        </div>
        """.formatted(greeting_vi, borrow.getBook().getTitle(), bodyLabel_vi, buildHtmlLateBorrowCard(borrow, vi),footer_vi);

        /*
         * =========================
         * English
         * =========================
         */

        String subject_en = messageSource.getMessage("email.borrow.reminder.late.subject", null, en);
        String greeting_en = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, en);
        String bodyLabel_en = messageSource.getMessage("email.borrow.reminder.due.late", new Object[]{late}, en);
        String footer_en = messageSource.getMessage("email.footer", null, en);
        String html_en = """
        <div style="padding:28px 30px;color:#25343F;">
            <!-- Section label -->
            <div style="font-size:13px; color:#B91C1C; text-transform:uppercase; letter-spacing:1px; margin-bottom:8px; font-weight:bold;">Overdue book</div>
            <!-- Greeting -->
            <h2 style="margin:0 0 20px; color:#25343F; font-size:22px;">%s</h2>
            <!-- Reminder -->
            <p style="font-size:15px; line-height:1.7; margin:0 0 20px;"><strong>%s</strong> %s</p>
            <!-- Late book card -->
            <div>%s</div>
            <!-- Footer text -->
            <p style=" font-size:14px; line-height:1.7; color:#59636E; margin-top:20px; margin-bottom:20px;">%s</p>
        </div>
        """.formatted(greeting_en, borrow.getBook().getTitle(), bodyLabel_en, buildHtmlLateBorrowCard(borrow, en) , footer_en
        );


        /*
         * =========================
         * French
         * =========================
         */
        String subject_fr = messageSource.getMessage("email.borrow.reminder.late.subject", null, fr);
        String greeting_fr = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, fr);
        String bodyLabel_fr = messageSource.getMessage("email.borrow.reminder.due.late", new Object[]{late}, fr);
        String footer_fr = messageSource.getMessage("email.footer",null,fr);
        String html_fr = """
        <div style="padding:28px 30px;color:#25343F;">
            <!-- Section label -->
            <div style="font-size:13px; color:#B91C1C; text-transform:uppercase; letter-spacing:1px; margin-bottom:8px; font-weight:bold;">Livre en retard</div>

            <!-- Greeting -->
            <h2 style="margin:0 0 20px; color:#25343F; font-size:22px;">%s</h2>

            <!-- Reminder -->
            <p style="font-size:15px; line-height:1.7; margin:0 0 20px;"><strong>%s</strong> %s</p>

            <!-- Late book card -->
            <div>%s<div>
            <!-- Footer text -->
            <p style=" font-size:14px; line-height:1.7; color:#59636E; margin-top:20px; margin-bottom:20px;">%s</p>
        </div>
        """.formatted(greeting_fr, borrow.getBook().getTitle(), bodyLabel_fr, buildHtmlLateBorrowCard(borrow, fr), footer_fr);

        String emailSubject = subject_vi + " | " + subject_en + " | " + subject_fr;

        String html = """
        <!DOCTYPE html>
        <html>
            <body style="margin:0; padding:0; background:#F4F6F8; font-family:Arial,Helvetica,sans-serif; color:#25343F;">
                <div style="max-width:620px; margin:35px auto; background:#FFFFFF; border-radius:12px; overflow:hidden; box-shadow:0 3px 12px rgba(0,0,0,0.08);">
                    <!-- Header -->
                    <div>%s</div>
                    <!-- Vietnamese -->
                    <div>%s</div>
                    <!-- Divider -->
                    <div style="margin:0 30px; border-top:2px solid #EEF1F4;"></div>
                    <!-- English -->
                    <div>%s</div>
                    <!-- Divider -->
                    <div style="margin:0 30px; border-top:2px solid #EEF1F4;"></div>
                    <!-- French -->
                    <div>%s</div>
                    <!-- Footer -->
                    <div>%s</div>
                </div>
            </body>
        </html>
        """.formatted(html_header_multi_lang, html_vi, html_en, html_fr, html_footer);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper( message,  true,  "UTF-8");
            helper.setTo(borrow.getUser().getEmail());
            helper.setSubject(emailSubject);
            helper.setText(html,true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"EMAIL-ISSUE",messageSource.getMessage("email.failed",null,LocaleContextHolder.getLocale()));
        }
    }

    public void sendBorrowDueReminder(Borrow borrow) {
        Locale vi = Locale.of("vi", "VN");
        String subject_vi = messageSource.getMessage("email.borrow.reminder.today.subject", null, vi);
        String greeting_vi = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, vi);
        String bodyLabel_vi = messageSource.getMessage("email.borrow.reminder.due.today", null, vi);
        String footer_vi = messageSource.getMessage("email.footer", null, vi);

        Locale en = Locale.US;
        String subject_en = messageSource.getMessage("email.borrow.reminder.today.subject", null, en);
        String greeting_en = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, en);
        String bodyLabel_en = messageSource.getMessage("email.borrow.reminder.due.today", null, en);
        String footer_en = messageSource.getMessage("email.footer", null, en);

        Locale fr = Locale.CANADA_FRENCH;
        String subject_fr = messageSource.getMessage("email.borrow.reminder.today.subject", null, fr);
        String greeting_fr = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, fr);
        String bodyLabel_fr = messageSource.getMessage("email.borrow.reminder.due.today", null, fr);
        String footer_fr = messageSource.getMessage("email.footer", null, fr);

        String emailSubject = subject_vi + " | " + subject_en + " | " +subject_fr;

        String html_vi = """
              <div style="padding:28px 30px;color:#25343F;">
                  <div style="font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;">Nhắc nhở mượn sách</div>
                  <h2 style="margin:0 0 20px;color:#25343F;font-size:22px;">%s</h2>
                  <p style="font-size:15px;line-height:1.7;margin:0 0 20px;"><strong>%s</strong> %s</p>
                  <div>%s<div>
                  <p style="font-size:14px;line-height:1.7;color:#59636E;margin-top:20px; margin-bottom:20px;">%s</p>
              </div>
        """.formatted(greeting_vi, borrow.getBook().getTitle(), bodyLabel_vi, buildHtmlBorrowCard(borrow, vi), footer_vi);

        String html_en = """
            <div style="padding:28px 30px;color:#25343F;">
                  <div style="font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;">Book Borrowing Reminder</div>
                  <h2 style="margin:0 0 20px;color:#25343F;font-size:22px;"> %s</h2>
                  <p style="font-size:15px;line-height:1.7;margin:0 0 20px;"><strong>%s</strong> %s</p>
                  <div>%s</div>
                  <p style="font-size:14px;line-height:1.7;color:#59636E;margin-top:20px; margin-bottom:20px;">%s</p>
              </div>
        """.formatted(greeting_en, borrow.getBook().getTitle(), bodyLabel_en, buildHtmlBorrowCard(borrow, en), footer_en);

        String html_fr = """
             <div style="padding:28px 30px;color:#25343F;">
                  <div style="font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;">Rappel d'emprunt</div>
                  <h2 style="margin:0 0 20px;color:#25343F;font-size:22px;">%s</h2>
                  <p style="font-size:15px;line-height:1.7;margin:0 0 20px;"><strong>%s</strong> %s</p>
                  <div>%s</div> 
                  <p style="font-size:14px;line-height:1.7;color:#59636E;margin-top:20px; margin-bottom:20px;">%s</p>
              </div>
        """.formatted(greeting_fr, borrow.getBook().getTitle(), bodyLabel_fr, buildHtmlBorrowCard(borrow, fr), footer_fr);

        String html = """
        <!DOCTYPE html>
        <html>
            <body style="margin:0; padding:0; background:#F4F6F8; font-family:Arial,Helvetica,sans-serif; color:#25343F;">
                <div style="max-width:620px; margin:35px auto; background:#FFFFFF; border-radius:12px; overflow:hidden; box-shadow:0 3px 12px rgba(0,0,0,0.08);">
                <!-- Header -->
                <div>%s</div>
                <!-- Vietnamese -->
                <div>%s</div>
                <!-- Divider -->
                <div style="margin:0 30px; border-top:2px solid #EEF1F4;"></div>
                <!-- English -->
                <div>%s</div>
                <!-- Divider -->
                <div style="margin:0 30px; border-top:2px solid #EEF1F4;"></div>
                <!-- French -->
                <div>%s</div>
        
                <!-- Footer -->
                <div>%s</div>
            </div>
            </body>
        </html>
        """.formatted(html_header_multi_lang, html_vi, html_en, html_fr, html_footer);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(borrow.getUser().getEmail());
            helper.setSubject(emailSubject);
            helper.setText(html, true);
            mailSender.send(message);
        }
        catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ISSUE", messageSource.getMessage("email.failed", null, LocaleContextHolder.getLocale()));
        }
    }

    public void sendBorrowCreatedEmail(Borrow borrow) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.borrow.created.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, locale);
        String body = messageSource.getMessage("email.borrow.created.body", null, locale);
        String reminder = messageSource.getMessage("email.borrow.created.return.remind", new Object[]{formatter.formatDateTime(borrow.getDueDate(), locale)}, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);


        String penaltyFeeAnnounce = messageSource.getMessage("email.borrow.created.penalty.announcement", null, locale);

        String html_body = """
              <div style="padding:28px 30px;color:#25343F;">
                  <h2 style="margin:0 0 20px;color:#25343F;font-size:22px;">%s</h2>
                  <p style="font-size:15px;line-height:1.7;margin:0 0 20px;">%s</p>
                  <div>%s<div>
                  <div style="margin-top:20px; margin-bottom:20px;">%s<div>
                  <p style="margin:0; font-size:11px; color:#9CA3AF;">%s</p>
                  <p style="font-size:14px;line-height:1.7;color:#59636E;margin-top:20px; margin-bottom:20px;">%s</p>
              </div>
        """.formatted(greeting, body, buildHtmlBorrowCard(borrow, locale), reminder,penaltyFeeAnnounce, footer);


        String html = """
        <!DOCTYPE html>
        <html>
            <body style="margin:0; padding:0; background:#F4F6F8; font-family:Arial,Helvetica,sans-serif; color:#25343F;">
                <div style="max-width:620px; margin:35px auto; background:#FFFFFF; border-radius:12px; overflow:hidden; box-shadow:0 3px 12px rgba(0,0,0,0.08);">
                <!-- Header -->
                <div>%s</div>
                <div>%s</div>
                <!-- Footer -->
                <div>%s</div>
            </div>
            </body>
        </html>
        """.formatted(html_single_lang, html_body, html_footer);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(borrow.getUser().getEmail());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            logger.log("Sent borrow created email to {}", borrow.getUser().getEmail());
        }
        catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ISSUE", messageSource.getMessage("email.failed", null, locale));
        }
    }

    public void sendBorrowReturned(Borrow borrow) {
        Locale vi = Locale.of("vi", "VN");
        String subject_vi = messageSource.getMessage("email.borrow.returned.subject", null, vi);
        String greeting_vi = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, vi);
        String bodyLabel_vi = messageSource.getMessage("email.borrow.returned.body", new Object[]{borrow.getBook().getTitle(), formatter.formatDateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")), vi)}, vi);
        String footer_vi = messageSource.getMessage("email.footer", null, vi);

        Locale en = Locale.US;
        String subject_en = messageSource.getMessage("email.borrow.returned.subject", null, en);
        String greeting_en = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, en);
        String bodyLabel_en = messageSource.getMessage("email.borrow.returned.body", new Object[]{borrow.getBook().getTitle(), formatter.formatDateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")), en)}, en);
        String footer_en = messageSource.getMessage("email.footer", null, en);

        Locale fr = Locale.CANADA_FRENCH;
        String subject_fr = messageSource.getMessage("email.borrow.returned.subject", null, fr);
        String greeting_fr = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, fr);
        String bodyLabel_fr = messageSource.getMessage("email.borrow.returned.body", new Object[]{borrow.getBook().getTitle(), formatter.formatDateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")), fr)}, fr);
        String footer_fr = messageSource.getMessage("email.footer", null, fr);

        String emailSubject = subject_vi + " | " + subject_en + " | " +subject_fr;

        String html_vi = """
              <div style="padding:28px 30px;color:#25343F;">
                  <div style="font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;">Nhắc nhở mượn sách</div>
                  <h2 style="margin:0 0 20px;color:#25343F;font-size:22px;">%s</h2>
                  <p style="font-size:15px;line-height:1.7;margin:0 0 20px;">%s</p>
                  <div>%s<div>
                  <p style="font-size:14px;line-height:1.7;color:#59636E;margin-top:20px; margin-bottom:20px;">%s</p>
              </div>
        """.formatted(greeting_vi, bodyLabel_vi, buildHtmlBorrowCard(borrow, vi), footer_vi);

        String html_en = """
            <div style="padding:28px 30px;color:#25343F;">
                  <div style="font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;">Book Borrowing Reminder</div>
                  <h2 style="margin:0 0 20px;color:#25343F;font-size:22px;"> %s</h2>
                  <p style="font-size:15px;line-height:1.7;margin:0 0 20px;">%s</p>
                  <div>%s</div>
                  <p style="font-size:14px;line-height:1.7;color:#59636E;margin-top:20px; margin-bottom:20px;">%s</p>
              </div>
        """.formatted(greeting_en, bodyLabel_en, buildHtmlBorrowCard(borrow, en), footer_en);

        String html_fr = """
             <div style="padding:28px 30px;color:#25343F;">
                  <div style="font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;">Rappel d'emprunt</div>
                  <h2 style="margin:0 0 20px;color:#25343F;font-size:22px;">%s</h2>
                  <p style="font-size:15px;line-height:1.7;margin:0 0 20px;">%s</p>
                  <div>%s</div>
                  <p style="font-size:14px;line-height:1.7;color:#59636E;margin-top:20px; margin-bottom:20px;">%s</p>
              </div>
        """.formatted(greeting_fr, bodyLabel_fr, buildHtmlBorrowCard(borrow, fr), footer_fr);

        String html = """
        <!DOCTYPE html>
        <html>
            <body style="margin:0; padding:0; background:#F4F6F8; font-family:Arial,Helvetica,sans-serif; color:#25343F;">
                <div style="max-width:620px; margin:35px auto; background:#FFFFFF; border-radius:12px; overflow:hidden; box-shadow:0 3px 12px rgba(0,0,0,0.08);">
                <!-- Header -->
                <div>%s</div>
                <!-- Vietnamese -->
                <div>%s</div>
                <!-- Divider -->
                <div style="margin:0 30px; border-top:2px solid #EEF1F4;"></div>
                <!-- English -->
                <div>%s</div>
                <!-- Divider -->
                <div style="margin:0 30px; border-top:2px solid #EEF1F4;"></div>
                <!-- French -->
                <div>%s</div>

                <!-- Footer -->
                <div>%s</div>
            </div>
            </body>
        </html>
        """.formatted(html_header_multi_lang, html_vi, html_en, html_fr, html_footer);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(borrow.getUser().getEmail());
            helper.setSubject(emailSubject);
            helper.setText(html, true);
            mailSender.send(message);
            logger.log("Sent borrow returned email to {}", borrow.getUser().getEmail());
        }
        catch (Exception e){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL-ISSUE", messageSource.getMessage("email.failed", null, LocaleContextHolder.getLocale()));
        }
    }
}
