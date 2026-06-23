package com.example.library_management.service.borrow;

import com.example.library_management.model.Borrow;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowReminderService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private MessageSource messageSource;

    @Scheduled(cron = "0 0 8 * * *") // runs every day at 8:00 AM
    public void sendDueDateReminders() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        List<Borrow> dueBorrows = borrowRepository.findByDueDateBetweenAndIsActiveTrue(startOfDay, endOfDay);

        for (Borrow borrow : dueBorrows) {
            String email = borrow.getUser().getEmail();
            String subject = "Book Due Today";
            String body = "Dear " + borrow.getUser().getUsername() + ",\n\n" +
                    "Your borrowed book \"" + borrow.getBook().getTitle() + "\" is due today.\n" +
                    "Please return it to avoid any penalties.\n\n" +
                    "Library Management System";

            mailService.sendEmail(email, subject, body);
        }
    }

    public String test() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        List<Borrow> borrows = borrowRepository.findByUserId(1);

        for (Borrow borrow : borrows) {
            String email = "baonamdao05@gmail.com";
            String subject = messageSource.getMessage("borrow.due.email.subject", null, LocaleContextHolder.getLocale());
            String body = messageSource.getMessage(
                    "borrow.due.email.body",
                    new Object[]{borrow.getUser().getUsername(), borrow.getBook().getTitle()},
                    LocaleContextHolder.getLocale()
            );

            mailService.sendEmail(email, subject, body);
            break;
        }
        return "Emails sent";
    }
}