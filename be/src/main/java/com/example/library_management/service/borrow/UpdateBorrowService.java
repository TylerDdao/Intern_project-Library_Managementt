package com.example.library_management.service.borrow;

import com.example.library_management.dto.request.BorrowRequest;
import com.example.library_management.dto.response.BorrowResponse;
import com.example.library_management.dto.response.RoleResponse;
import com.example.library_management.model.*;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.repository.PolicyRepository;
import com.example.library_management.service.MailService;
import com.example.library_management.service.mail.BorrowMailService;
import com.example.library_management.util.AuditLogger;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UpdateBorrowService {
    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private AuditLogger logger;

    @Autowired
    private BorrowMailService borrowMailService;

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0 * * *")
    public void calculateLatePenalty(){
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        List<Borrow> lateBorrows = borrowRepository.findByDueDateLessThanAndIsActiveTrue(startOfDay);
        Policy latePenalty = policyRepository.findByKey("late_penalty_per_day")
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("error.policy.not.found", null, LocaleContextHolder.getLocale())
                ));
        float penaltyAmount = Float.parseFloat(latePenalty.getValue());
        for (Borrow borrow : lateBorrows) {
            long lateDays = ChronoUnit.DAYS.between(borrow.getDueDate().toLocalDate(), LocalDate.now());
            borrow.setPenalty((float) lateDays * penaltyAmount);
        }
        borrowRepository.saveAll(lateBorrows);
    }

    @Transactional
    public BorrowResponse returnBorrow(BorrowRequest request){
        Borrow borrow = borrowRepository.findById(request.getId()).orElseThrow(()-> new RuntimeException(messageSource.getMessage("error.borrow.not.found", null, LocaleContextHolder.getLocale())));

        borrow.setIsActive(request.getIsActive());
        Book book = bookRepository.findByIdForUpdate(borrow.getBook().getId()).orElseThrow(() ->new RuntimeException(messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));
        book.setCopies(book.getCopies() + 1);
        borrowRepository.save(borrow);
        bookRepository.save(book);

        logger.log("Returned borrow ID #{}", borrow.getId());
        borrowMailService.sendBorrowReturned(borrow);
        return new BorrowResponse(borrow);
    }

    public BorrowResponse updateBorrow(BorrowRequest request){
        Borrow borrow = borrowRepository.findById(request.getId()).orElseThrow(()-> new RuntimeException(messageSource.getMessage("error.borrow.not.found", null, LocaleContextHolder.getLocale())));

        if (request.getDueDate() != null) {
            borrow.setDueDate(request.getDueDate());
        }

        borrowRepository.save(borrow);

        logger.log("Updated borrow ID #{}", borrow.getId());
        return new BorrowResponse(borrow);
    }
}
