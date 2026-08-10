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
    AuditLogger log;

    @Scheduled(cron = "0 0 8 * * *") // runs every day at 8:00 AM
    public void sendDueDateReminders() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        List<Borrow> dueBorrows = borrowRepository.findByDueDateBetweenAndIsActiveTrue(startOfDay, endOfDay);

        for (Borrow borrow : dueBorrows) {
            try{
                mailService.sendBorrowDueReminder(borrow);
            }
            catch (Exception e){
                log.error("Failed to send reminder for borrow id {}", borrow.getId(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * *") // runs every day at 8:00 AM
    public void sendLateDueDateReminders() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        List<Borrow> lateBorrows = borrowRepository.findByDueDateLessThanAndIsActiveTrue(startOfDay);

        for (Borrow borrow : lateBorrows) {
            try {
                mailService.sendLateBorrowReminder(borrow);
            } catch (Exception e) {
                log.error("Failed to send reminder for borrow id {}", borrow.getId(), e);
            }
        }
    }
}