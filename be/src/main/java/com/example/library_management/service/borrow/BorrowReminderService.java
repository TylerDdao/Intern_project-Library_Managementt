package com.example.library_management.service.borrow;

import com.example.library_management.model.Borrow;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.service.MailService;
import com.example.library_management.util.AuditLogger;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class BorrowReminderService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    AuditLogger logger;

    @Scheduled(cron = "0 0 8 * * *") // runs every day at 8:00 AM
    public void sendDueDateReminders() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        List<Borrow> dueBorrows = borrowRepository.findByDueDateBetweenAndIsActiveTrue(startOfDay, endOfDay);

        for (Borrow borrow : dueBorrows) {
            try {
                mailService.sendBorrowDueReminder(borrow.getUser().getEmail(), borrow.getUser().getFullName(), borrow.getBook().getTitle(), LocaleContextHolder.getLocale());
            }
            catch (MessagingException e){
                logger.error("An error has occurred while sending borrow reminder emails: " + e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * *") // runs every day at 8:00 AM
    public void sendLateDueDateReminders() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        List<Borrow> lateBorrows = borrowRepository.findByDueDateLessThanAndIsActiveTrue(startOfDay);

        for (Borrow borrow : lateBorrows) {
            try {
                long lateDays = ChronoUnit.DAYS.between(borrow.getDueDate().toLocalDate(), LocalDate.now());
                mailService.sendLateBorrowReminder(borrow.getUser().getEmail(), borrow.getUser().getFullName(), borrow.getBook().getTitle(), lateDays, LocaleContextHolder.getLocale());
            }
            catch (MessagingException e){
                logger.error("An error has occurred while sending borrow reminder emails: " + e.getMessage());
            }
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