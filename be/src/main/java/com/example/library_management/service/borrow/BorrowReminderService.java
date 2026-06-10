package com.example.library_management.service.borrow;

import com.example.library_management.model.Borrow;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowReminderService {

    private final BorrowRepository borrowRepository;
    private final MailService mailService;

    @Scheduled(cron = "0 0 8 * * *") // runs every day at 8:00 AM
    public void sendDueDateReminders() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        List<Borrow> dueBorrows = borrowRepository.findByDueDateBetween(startOfDay, endOfDay);

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
}