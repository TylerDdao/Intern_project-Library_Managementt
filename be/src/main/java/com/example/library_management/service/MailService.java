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

    public void sendWelcomeEmail(User user) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.welcome.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{user.getFullName()}, locale);
        String body = messageSource.getMessage("email.welcome.body", null, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);

        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background:#2C5EAD;padding:28px 25px;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
                        Library Management System
                    </h1>
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
                <div style="background:#2C5EAD;padding:28px 25px;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
                        Library Management System
                    </h1>
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

    public void sendLateBorrowReminder(Borrow borrow) {
        Long late = formatter.calculateDaysBetween(
                borrow.getDueDate().toLocalDate(),
                LocalDate.now()
        );
        Book book = borrow.getBook();
        String bookTitle = book.getTitle();
        String bookAuthor = book.getAuthor();
        String bookCoverFile = book.getCoverUrl() != null
                ? book.getCoverUrl()
                : "default.jpg";
        String bookCoverUrl = backEndUrl + "/book-covers/" + bookCoverFile;

        Optional<Policy> penaltyPolicy = policyRepository.findByKey("late_penalty_per_day");
        Locale vi = Locale.of("vi", "VN");
        Locale en = Locale.US;
        Locale fr = Locale.CANADA_FRENCH;
        String subject_vi = messageSource.getMessage(
                "email.borrow.reminder.late.subject",
                null,
                vi
        );
        String greeting_vi = messageSource.getMessage(
                "email.greeting.name",
                new Object[]{borrow.getUser().getFullName()},
                vi
        );
        String bodyLabel_vi = messageSource.getMessage(
                "email.borrow.reminder.due.late",
                new Object[]{late},
                vi
        );
        String borrowedAt_vi = messageSource.getMessage(
                "email.borrow.at",
                new Object[]{
                        formatter.formatDateTime(
                                borrow.getCreatedAt(),
                                vi
                        )
                },
                vi
        );
        String penaltyFee_vi = messageSource.getMessage(
                "email.borrow.late.penalty",
                new Object[]{
                        formatter.formatVND(borrow.getPenalty())
                },
                vi
        );
        String penaltyRate_vi;
        if (penaltyPolicy.isPresent()) {
            penaltyRate_vi = messageSource.getMessage(
                    "email.borrow.penalty.fee",
                    new Object[]{
                            formatter.formatVND(
                                    Long.parseLong(penaltyPolicy.get().getValue())
                            )
                    },
                    vi
            );
        } else {
            penaltyRate_vi = messageSource.getMessage(
                    "error.Unknown",
                    null,
                    vi
            );
        }
        String footer_vi = messageSource.getMessage(
                "email.footer",
                null,
                vi
        );

        String html_vi = """
        <div style="padding:28px 30px;color:#25343F;">

            <!-- Section label -->
            <div style="
                font-size:13px;
                color:#B91C1C;
                text-transform:uppercase;
                letter-spacing:1px;
                margin-bottom:8px;
                font-weight:bold;
            ">
                Sách quá hạn
            </div>

            <!-- Greeting -->
            <h2 style="
                margin:0 0 20px;
                color:#25343F;
                font-size:22px;
            ">
                %s
            </h2>

            <!-- Reminder -->
            <p style="
                font-size:15px;
                line-height:1.7;
                margin:0 0 20px;
            ">
                <strong>%s</strong> %s
            </p>

            <!-- Late book card -->
            <div style="
                background:#FEF2F2;
                border-left:4px solid #DC2626;
                padding:16px 18px;
                margin:20px 0;
                border-radius:6px;
            ">

                <!-- Label -->
                <div style="
                    font-size:12px;
                    color:#B91C1C;
                    text-transform:uppercase;
                    letter-spacing:.5px;
                    margin-bottom:12px;
                    font-weight:bold;
                ">
                    Sách quá hạn
                </div>

                <!-- Book information -->
                <div style="
                    display:flex;
                    align-items:flex-start;
                ">

                    <img
                        src="%s"
                        style="
                            width:100px;
                            height:145px;
                            object-fit:cover;
                            border-radius:5px;
                            display:block;
                        "
                    />

                    <div style="
                        margin-left:15px;
                        flex:1;
                    ">

                        <!-- Title -->
                        <div style="
                            font-size:18px;
                            font-weight:bold;
                            color:#991B1B;
                            margin-bottom:4px;
                        ">
                            %s
                        </div>

                        <!-- Author -->
                        <div style="
                            font-size:14px;
                            color:#7F1D1D;
                            margin-bottom:14px;
                        ">
                            %s
                        </div>

                        <!-- Borrow details -->
                        <div style="
                            font-size:13px;
                            line-height:1.8;
                            color:#4B5563;
                        ">

                            <div>
                                <strong style="color:#374151;">
                                    Ngày mượn:
                                </strong>
                                %s
                            </div>

                            <div>
                                <strong style="color:#B91C1C;">
                                    Quá hạn:
                                </strong>

                                <span style="
                                    color:#B91C1C;
                                    font-weight:bold;
                                ">
                                    %s ngày
                                </span>
                            </div>

                            <div>
                                <strong style="color:#374151;">
                                    Tiền phạt:
                                </strong>
                                %s
                            </div>

                            <div>
                                <strong style="color:#374151;">
                                    Mức phạt:
                                </strong>
                                %s
                            </div>

                        </div>
                    </div>
                </div>

                <!-- Warning -->
                <div style="
                    margin-top:18px;
                    padding-top:14px;
                    border-top:1px solid #FECACA;
                    font-size:13px;
                    color:#991B1B;
                ">
                    Vui lòng trả sách sớm nhất có thể để tránh phát sinh thêm tiền phạt.
                </div>

            </div>

            <!-- Footer text -->
            <p style="
                font-size:14px;
                line-height:1.7;
                color:#59636E;
                margin:20px 0 0;
            ">
                %s
            </p>

            <!-- Signature -->
            <div style="
                border-top:1px solid #E5E7EB;
                margin-top:28px;
                padding-top:18px;
                text-align:right;
            ">
                <small style="color:#6B7280;">
                    Library Management Team
                </small>
            </div>

        </div>
        """.formatted(
                greeting_vi,
                borrow.getBook().getTitle(),
                bodyLabel_vi,
                bookCoverUrl,
                bookTitle,
                bookAuthor,
                borrowedAt_vi,
                late,
                penaltyFee_vi,
                penaltyRate_vi,
                footer_vi
        );


        /*
         * =========================
         * English
         * =========================
         */

        String subject_en = messageSource.getMessage(
                "email.borrow.reminder.late.subject",
                null,
                en
        );
        String greeting_en = messageSource.getMessage(
                "email.greeting.name",
                new Object[]{borrow.getUser().getFullName()},
                en
        );
        String bodyLabel_en = messageSource.getMessage(
                "email.borrow.reminder.due.late",
                new Object[]{late},
                en
        );
        String borrowedAt_en = messageSource.getMessage(
                "email.borrow.at",
                new Object[]{
                        formatter.formatDateTime(
                                borrow.getCreatedAt(),
                                en
                        )
                },
                en
        );
        String penaltyFee_en = messageSource.getMessage(
                "email.borrow.late.penalty",
                new Object[]{
                        formatter.formatVND(borrow.getPenalty())
                },
                en
        );
        String penaltyRate_en;
        if (penaltyPolicy.isPresent()) {
            penaltyRate_en = messageSource.getMessage(
                    "email.borrow.penalty.fee",
                    new Object[]{
                            formatter.formatVND(
                                    Long.parseLong(penaltyPolicy.get().getValue())
                            )
                    },
                    en
            );
        } else {
            penaltyRate_en = messageSource.getMessage(
                    "error.Unknown",
                    null,
                    en
            );
        }
        String footer_en = messageSource.getMessage(
                "email.footer",
                null,
                en
        );

        String html_en = """
        <div style="padding:28px 30px;color:#25343F;">

            <div style="
                font-size:13px;
                color:#B91C1C;
                text-transform:uppercase;
                letter-spacing:1px;
                margin-bottom:8px;
                font-weight:bold;
            ">
                Overdue Book
            </div>

            <h2 style="
                margin:0 0 20px;
                color:#25343F;
                font-size:22px;
            ">
                %s
            </h2>

            <p style="
                font-size:15px;
                line-height:1.7;
                margin:0 0 20px;
            ">
                <strong>%s</strong> %s
            </p>

            <!-- Late book card -->
            <div style="
                background:#FEF2F2;
                border-left:4px solid #DC2626;
                padding:16px 18px;
                margin:20px 0;
                border-radius:6px;
            ">

                <div style="
                    font-size:12px;
                    color:#B91C1C;
                    text-transform:uppercase;
                    letter-spacing:.5px;
                    margin-bottom:12px;
                    font-weight:bold;
                ">
                    Overdue Book
                </div>

                <div style="
                    display:flex;
                    align-items:flex-start;
                ">

                    <img
                        src="%s"
                        style="
                            width:100px;
                            height:145px;
                            object-fit:cover;
                            border-radius:5px;
                            display:block;
                        "
                    />

                    <div style="
                        margin-left:15px;
                        flex:1;
                    ">

                        <div style="
                            font-size:18px;
                            font-weight:bold;
                            color:#991B1B;
                            margin-bottom:4px;
                        ">
                            %s
                        </div>

                        <div style="
                            font-size:14px;
                            color:#7F1D1D;
                            margin-bottom:14px;
                        ">
                            %s
                        </div>

                        <div style="
                            font-size:13px;
                            line-height:1.8;
                            color:#4B5563;
                        ">

                            <div>
                                <strong style="color:#374151;">
                                    Borrowed on:
                                </strong>
                                %s
                            </div>

                            <div>
                                <strong style="color:#B91C1C;">
                                    Late:
                                </strong>

                                <span style="
                                    color:#B91C1C;
                                    font-weight:bold;
                                ">
                                    %s days
                                </span>
                            </div>

                            <div>
                                <strong style="color:#374151;">
                                    Penalty:
                                </strong>
                                %s
                            </div>

                            <div>
                                <strong style="color:#374151;">
                                    Penalty rate:
                                </strong>
                                %s
                            </div>

                        </div>
                    </div>
                </div>

                <div style="
                    margin-top:18px;
                    padding-top:14px;
                    border-top:1px solid #FECACA;
                    font-size:13px;
                    color:#991B1B;
                ">
                    Please return this book as soon as possible to avoid additional penalties.
                </div>

            </div>

            <p style="
                font-size:14px;
                line-height:1.7;
                color:#59636E;
                margin:20px 0 0;
            ">
                %s
            </p>

            <div style="
                border-top:1px solid #E5E7EB;
                margin-top:28px;
                padding-top:18px;
                text-align:right;
            ">
                <small style="color:#6B7280;">
                    Library Management Team
                </small>
            </div>

        </div>
        """.formatted(
                greeting_en,
                borrow.getBook().getTitle(),
                bodyLabel_en,
                bookCoverUrl,
                bookTitle,
                bookAuthor,
                borrowedAt_en,
                late,
                penaltyFee_en,
                penaltyRate_en,
                footer_en
        );


        /*
         * =========================
         * French
         * =========================
         */

        String subject_fr = messageSource.getMessage(
                "email.borrow.reminder.late.subject",
                null,
                fr
        );

        String greeting_fr = messageSource.getMessage(
                "email.greeting.name",
                new Object[]{borrow.getUser().getFullName()},
                fr
        );

        String bodyLabel_fr = messageSource.getMessage(
                "email.borrow.reminder.due.late",
                new Object[]{late},
                fr
        );

        String borrowedAt_fr = messageSource.getMessage(
                "email.borrow.at",
                new Object[]{
                        formatter.formatDateTime(
                                borrow.getCreatedAt(),
                                fr
                        )
                },
                fr
        );
        String penaltyFee_fr = messageSource.getMessage(
                "email.borrow.late.penalty",
                new Object[]{
                        formatter.formatVND(borrow.getPenalty())
                },
                fr
        );
        String penaltyRate_fr;
        if (penaltyPolicy.isPresent()) {
            penaltyRate_fr = messageSource.getMessage(
                    "email.borrow.penalty.fee",
                    new Object[]{
                            formatter.formatVND(
                                    Long.parseLong(penaltyPolicy.get().getValue())
                            )
                    },
                    fr
            );
        } else {
            penaltyRate_fr = messageSource.getMessage(
                    "error.Unknown",
                    null,
                    fr
            );
        }
        String footer_fr = messageSource.getMessage(
                "email.footer",
                null,
                fr
        );

        String html_fr = """
        <div style="padding:28px 30px;color:#25343F;">

            <div style="
                font-size:13px;
                color:#B91C1C;
                text-transform:uppercase;
                letter-spacing:1px;
                margin-bottom:8px;
                font-weight:bold;
            ">
                Livre en retard
            </div>

            <h2 style="
                margin:0 0 20px;
                color:#25343F;
                font-size:22px;
            ">
                %s
            </h2>

            <p style="
                font-size:15px;
                line-height:1.7;
                margin:0 0 20px;
            ">
                <strong>%s</strong> %s
            </p>

            <!-- Late book card -->
            <div style="
                background:#FEF2F2;
                border-left:4px solid #DC2626;
                padding:16px 18px;
                margin:20px 0;
                border-radius:6px;
            ">

                <div style="
                    font-size:12px;
                    color:#B91C1C;
                    text-transform:uppercase;
                    letter-spacing:.5px;
                    margin-bottom:12px;
                    font-weight:bold;
                ">
                    Livre en retard
                </div>

                <div style="
                    display:flex;
                    align-items:flex-start;
                ">

                    <img
                        src="%s"
                        style="
                            width:100px;
                            height:145px;
                            object-fit:cover;
                            border-radius:5px;
                            display:block;
                        "
                    />

                    <div style="
                        margin-left:15px;
                        flex:1;
                    ">

                        <div style="
                            font-size:18px;
                            font-weight:bold;
                            color:#991B1B;
                            margin-bottom:4px;
                        ">
                            %s
                        </div>

                        <div style="
                            font-size:14px;
                            color:#7F1D1D;
                            margin-bottom:14px;
                        ">
                            %s
                        </div>
                        <div style="
                            font-size:13px;
                            line-height:1.8;
                            color:#4B5563;
                        ">
                            <div>
                                <strong style="color:#374151;">
                                    Emprunté le :
                                </strong>
                                %s
                            </div>

                            <div>
                                <strong style="color:#B91C1C;">
                                    Retard :
                                </strong>

                                <span style="
                                    color:#B91C1C;
                                    font-weight:bold;
                                ">
                                    %s jours
                                </span>
                            </div>

                            <div>
                                <strong style="color:#374151;">
                                    Pénalité :
                                </strong>
                                %s
                            </div>

                            <div>
                                <strong style="color:#374151;">
                                    Taux de pénalité :
                                </strong>
                                %s
                            </div>

                        </div>
                    </div>
                </div>
                <div style="
                    margin-top:18px;
                    padding-top:14px;
                    border-top:1px solid #FECACA;
                    font-size:13px;
                    color:#991B1B;
                ">
                    Veuillez retourner ce livre dès que possible afin d'éviter des pénalités supplémentaires.
                </div>

            </div>
            <p style="
                font-size:14px;
                line-height:1.7;
                color:#59636E;
                margin:20px 0 0;
            ">
                %s
            </p>
            <div style="
                border-top:1px solid #E5E7EB;
                margin-top:28px;
                padding-top:18px;
                text-align:right;
            ">
                <small style="color:#6B7280;">
                    Library Management Team
                </small>
            </div>
        </div>
        """.formatted(
                greeting_fr,
                borrow.getBook().getTitle(),
                bodyLabel_fr,
                bookCoverUrl,
                bookTitle,
                bookAuthor,
                borrowedAt_fr,
                late,
                penaltyFee_fr,
                penaltyRate_fr,
                footer_fr
        );
        String emailSubject =
                subject_vi + " | " +
                subject_en + " | " +
                subject_fr;

        String html = """
        <!DOCTYPE html>
        <html>
        <body style="
            margin:0;
            padding:0;
            background:#F4F6F8;
            font-family:Arial,Helvetica,sans-serif;
            color:#25343F;
        ">
            <div style="
                max-width:620px;
                margin:35px auto;
                background:#FFFFFF;
                border-radius:12px;
                overflow:hidden;
                box-shadow:0 3px 12px rgba(0,0,0,0.08);
            ">
                <!-- Header -->
                <div style="
                    background:#2C5EAD;
                    padding:28px 25px;
                    text-align:center;
                ">
                    <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
                        Library Management System
                    </h1>
                </div>
                <!-- Language navigation -->
                <div style="padding:16px 25px;background:#F8FAFC;border-bottom:1px solid #E5E7EB;text-align:center;">
                    <span style="font-size:12px;color:#6B7280;">
                        Tiếng Việt
                    </span>
                    <span style="
                        color:#CBD5E1;
                        margin:0 10px;
                    ">
                        •
                    </span>
                    <span style="
                        font-size:12px;
                        color:#6B7280;
                    ">
                        English
                    </span>
                    <span style="
                        color:#CBD5E1;
                        margin:0 10px;
                    ">
                        •
                    </span>
                    <span style="
                        font-size:12px;
                        color:#6B7280;
                    ">
                        Français
                    </span>
                </div>
                <!-- Vietnamese -->
                <div>
                    %s
                </div>
                <!-- Divider -->
                <div style="
                    margin:0 30px;
                    border-top:2px solid #EEF1F4;
                "></div>
                <!-- English -->
                <div>
                    %s
                </div>
                <!-- Divider -->
                <div style="
                    margin:0 30px;
                    border-top:2px solid #EEF1F4;
                "></div>
                <!-- French -->
                <div>
                    %s
                </div>
                <!-- Footer -->
                <div style="
                    background:#F8FAFC;
                    padding:18px 25px;
                    text-align:center;
                    border-top:1px solid #E5E7EB;
                ">
                    <p style="
                        margin:0;
                        font-size:11px;
                        color:#9CA3AF;
                    ">
                        This is an automated message. Please do not reply to this email.
                    </p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(html_vi, html_en, html_fr);

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setTo(
                    borrow.getUser().getEmail()
            );

            helper.setSubject(emailSubject);

            helper.setText(
                    html,
                    true
            );

            mailSender.send(message);

        } catch (Exception e) {

            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "EMAIL-ISSUE",
                    messageSource.getMessage(
                            "email.failed",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }
    }

    public void sendBorrowDueReminder(Borrow borrow) {
        Locale vi = Locale.of("vi", "VN");
        String subject_vi = messageSource.getMessage("email.borrow.reminder.today.subject", null, vi);
        String greeting_vi = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, vi);
        String bodyLabel_vi = messageSource.getMessage("email.borrow.reminder.due.today", null, vi);
        String borrowedAt_vi = messageSource.getMessage("email.borrow.at", new Object[]{formatter.formatDateTime(borrow.getCreatedAt(), vi)}, vi);
        String footer_vi = messageSource.getMessage("email.footer", null, vi);

        Locale en = Locale.US;
        String subject_en = messageSource.getMessage("email.borrow.reminder.today.subject", null, en);
        String greeting_en = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, en);
        String bodyLabel_en = messageSource.getMessage("email.borrow.reminder.due.today", null, en);
        String borrowedAt_en = messageSource.getMessage("email.borrow.at", new Object[]{formatter.formatDateTime(borrow.getCreatedAt(), en)}, en);
        String footer_en = messageSource.getMessage("email.footer", null, en);

        Locale fr = Locale.CANADA_FRENCH;
        String subject_fr = messageSource.getMessage("email.borrow.reminder.today.subject", null, fr);
        String greeting_fr = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, fr);
        String bodyLabel_fr = messageSource.getMessage("email.borrow.reminder.due.today", null, fr);
        String borrowedAt_fr = messageSource.getMessage("email.borrow.at", new Object[]{formatter.formatDateTime(borrow.getCreatedAt(), fr)}, fr);
        String footer_fr = messageSource.getMessage("email.footer", null, fr);

        String emailSubject = subject_vi + " | " + subject_en + " | " +subject_fr;

        String html_vi = """
              <div style="padding:28px 30px;color:#25343F;">
                  <div style="font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;">
                      Nhắc nhở mượn sách
                  </div>
        
                  <h2 style="margin:0 0 20px;color:#25343F;font-size:22px;">
                      %s
                  </h2>
        
                  <p style="font-size:15px;line-height:1.7;margin:0 0 20px;">
                      <strong>%s</strong> %s
                  </p>
        
                  <div style="background:#F3F7F2;border-left:4px solid #5C8D5A;padding:16px 18px;margin:20px 0;border-radius:6px;">
                      <div style="font-size:12px;color:#6B7280;text-transform:uppercase;letter-spacing:.5px;margin-bottom:5px;">
                          Sách
                      </div>
                      <div style="display: flex;">
                          <img src="http://localhost:8080/api/book-covers/1.jpg" style="width: 100px;"/>
                          <div style="margin-left: 10px;">
                              <div style="font-size:17px;font-weight:bold;color:#25343F; ">
                              %s
                          </div>
                          <div style="font-size:17px;color:#25343F;">
                              %s
                          </div>
                          <div style="font-size:17px;color:#25343F;">
                              %s
                          </div>
                          </div>
                      </div>
                  </div>
        
                  <p style="font-size:14px;line-height:1.7;color:#59636E;margin:20px 0 0;">
                      %s
                  </p>
        
                  <div style="border-top:1px solid #E5E7EB;margin-top:28px;padding-top:18px;text-align:right;">
                      <small style="color:#6B7280;">
                          Library Management Team
                      </small>
                  </div>
              </div>
        """.formatted(
                greeting_vi,
                borrow.getBook().getTitle(),
                bodyLabel_vi,
                borrow.getBook().getTitle(),
                borrow.getBook().getAuthor(),
                borrowedAt_vi,
                footer_vi
        );

        String html_en = """
            <div style="padding:28px 30px;color:#25343F;">
                  <div style="font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;">
                      Book Borrowing Reminder
                  </div>
        
                  <h2 style="margin:0 0 20px;color:#25343F;font-size:22px;">
                      %s
                  </h2>
        
                  <p style="font-size:15px;line-height:1.7;margin:0 0 20px;">
                      <strong>%s</strong> %s
                  </p>
        
                  <div style="background:#F3F7F2;border-left:4px solid #5C8D5A;padding:16px 18px;margin:20px 0;border-radius:6px;">
                      <div style="font-size:12px;color:#6B7280;text-transform:uppercase;letter-spacing:.5px;margin-bottom:5px;">
                          Book
                      </div>
                      <div style="display: flex;">
                          <img src="http://localhost:8080/api/book-covers/1.jpg" style="width: 100px;"/>
                          <div style="margin-left: 10px;">
                              <div style="font-size:17px;font-weight:bold;color:#25343F; ">
                              %s
                          </div>
                          <div style="font-size:17px;color:#25343F;">
                              %s
                          </div>
                          <div style="font-size:17px;color:#25343F;">
                              %s
                          </div>
                          </div>
                      </div>
                  </div>
        
                  <p style="font-size:14px;line-height:1.7;color:#59636E;margin:20px 0 0;">
                      %s
                  </p>
        
                  <div style="border-top:1px solid #E5E7EB;margin-top:28px;padding-top:18px;text-align:right;">
                      <small style="color:#6B7280;">
                          Library Management Team
                      </small>
                  </div>
              </div>
        """.formatted(
                greeting_en,
                borrow.getBook().getTitle(),
                bodyLabel_en,
                borrow.getBook().getTitle(),
                borrow.getBook().getAuthor(),
                borrowedAt_en,
                footer_en
        );

        String html_fr = """
             <div style="padding:28px 30px;color:#25343F;">
                  <div style="font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;">
                      Rappel d'emprunt
                  </div>
        
                  <h2 style="margin:0 0 20px;color:#25343F;font-size:22px;">
                      %s
                  </h2>
        
                  <p style="font-size:15px;line-height:1.7;margin:0 0 20px;">
                      <strong>%s</strong> %s
                  </p>
        
                  <div style="background:#F3F7F2;border-left:4px solid #5C8D5A;padding:16px 18px;margin:20px 0;border-radius:6px;">
                      <div style="font-size:12px;color:#6B7280;text-transform:uppercase;letter-spacing:.5px;margin-bottom:5px;">
                          Livre
                      </div>
                      <div style="display: flex;">
                          <img src="http://localhost:8080/api/book-covers/1.jpg" style="width: 100px;"/>
                          <div style="margin-left: 10px;">
                              <div style="font-size:17px;font-weight:bold;color:#25343F; ">
                              %s
                          </div>
                          <div style="font-size:17px;color:#25343F;">
                              %s
                          </div>
                          <div style="font-size:17px;color:#25343F;">
                              %s
                          </div>
                          </div>
                      </div>
                  </div>
        
                  <p style="font-size:14px;line-height:1.7;color:#59636E;margin:20px 0 0;">
                      %s
                  </p>
        
                  <div style="border-top:1px solid #E5E7EB;margin-top:28px;padding-top:18px;text-align:right;">
                      <small style="color:#6B7280;">
                          Library Management Team
                      </small>
                  </div>
              </div>
        """.formatted(
                greeting_fr,
                borrow.getBook().getTitle(),
                bodyLabel_fr,
                borrow.getBook().getTitle(),
                borrow.getBook().getAuthor(),
                borrowedAt_fr,
                footer_fr
        );

        String html = """
        <!DOCTYPE html>
        <html>
        <body style="margin:0;padding:0;background:#F4F6F8;font-family:Arial,Helvetica,sans-serif;color:#25343F;">

            <div style="max-width:620px;margin:35px auto;background:#FFFFFF;border-radius:12px;overflow:hidden;box-shadow:0 3px 12px rgba(0,0,0,0.08);">

                <!-- Header -->
                <div style="background:#2C5EAD;padding:28px 25px;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
                        Library Management System
                    </h1>
                </div>

                <!-- Language navigation -->
                <div style="padding:16px 25px;background:#F8FAFC;border-bottom:1px solid #E5E7EB;text-align:center;">
                    <span style="font-size:12px;color:#6B7280;">
                        Tiếng Việt
                    </span>
                    <span style="color:#CBD5E1;margin:0 10px;">•</span>
                    <span style="font-size:12px;color:#6B7280;">
                        English
                    </span>
                    <span style="color:#CBD5E1;margin:0 10px;">•</span>
                    <span style="font-size:12px;color:#6B7280;">
                        Français
                    </span>
                </div>

                <!-- Vietnamese -->
                <div>
                    %s
                </div>

                <!-- Divider -->
                <div style="margin:0 30px;border-top:2px solid #EEF1F4;"></div>

                <!-- English -->
                <div>
                    %s
                </div>

                <!-- Divider -->
                <div style="margin:0 30px;border-top:2px solid #EEF1F4;"></div>

                <!-- French -->
                <div>
                    %s
                </div>

                <!-- Footer -->
                <div style="background:#F8FAFC;padding:18px 25px;text-align:center;border-top:1px solid #E5E7EB;">
                    <p style="margin:0;font-size:11px;color:#9CA3AF;">
                        This is an automated message. Please do not reply to this email.
                    </p>
                </div>

            </div>

        </body>
        </html>
        """.formatted(html_vi, html_en, html_fr);

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

    public void sendPasswordChangedEmail(User user) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String now = formatter.formatDateTime(LocalDateTime.now(), locale);
        String subject = messageSource.getMessage("email.password.changed.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{user.getFullName()}, locale);
        String bodyLabel = messageSource.getMessage("email.password.changed.body", new Object[]{now}, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);
        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background:#2C5EAD;padding:28px 25px;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
                        Library Management System
                    </h1>
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
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String to, String resetLink, Integer activeDuration) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.reset.password.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting", null, locale);
        String bodyLabel = messageSource.getMessage("email.reset.password.body", new Object[]{activeDuration}, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);
        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background:#2C5EAD;padding:28px 25px;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
                        Library Management System
                    </h1>
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
                <div style="background:#2C5EAD;padding:28px 25px;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
                        Library Management System
                    </h1>
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
        String borrowOn = messageSource.getMessage("email.borrow.at", new Object[]{formatter.formatDateTime(borrow.getCreatedAt(), locale)}, LocaleContextHolder.getLocale());
        String returnRemind = messageSource.getMessage("email.borrow.created.return.remind", null, LocaleContextHolder.getLocale());
        String dueOn = formatter.formatDateTime(borrow.getDueDate(), locale);
        Policy penalty;
        String penaltyFee;
        if(policyRepository.findByKey("late_penalty_per_day").isPresent()){
            penalty = policyRepository.findByKey("late_penalty_per_day").orElse(new Policy());
            penaltyFee = messageSource.getMessage("email.borrow.penalty.fee", new Object[]{formatter.formatVND(Long.parseLong(penalty.getValue()))}, locale);
        }
        else {
            penaltyFee = messageSource.getMessage("email.borrow.penalty.fee", new Object[]{messageSource.getMessage("error.Unknown", null, LocaleContextHolder.getLocale())}, locale);
        }
        String penaltyFeeAnnounce = messageSource.getMessage("email.borrow.created.penalty.announcement", null, locale);


        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background:#2C5EAD;padding:28px 25px;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
                        Library Management System
                    </h1>
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
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.borrow.returned.subject", null, locale);
        String greeting = messageSource.getMessage("email.greeting.name", new Object[]{borrow.getUser().getFullName()}, locale);
        String body = messageSource.getMessage("email.borrow.returned.body", new Object[]{formatter.formatDateTime(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")), locale)}, locale);
        String footer = messageSource.getMessage("email.footer", null, locale);

        String title = borrow.getBook().getTitle();
        String bookCoverFile = borrow.getBook().getCoverUrl() != null
                ? borrow.getBook().getCoverUrl()
                : "default.jpg";
        String author = borrow.getBook().getAuthor();


        String html = """
            <div style="max-width:600px;margin:40px auto;font-family:Arial,sans-serif;">
                <div style="background:#2C5EAD;padding:28px 25px;text-align:center;">
                    <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
                        Library Management System
                    </h1>
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