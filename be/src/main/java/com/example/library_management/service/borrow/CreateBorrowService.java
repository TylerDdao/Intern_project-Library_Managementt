package com.example.library_management.service.borrow;

import com.example.library_management.dto.request.borrow.BorrowRequest;
import com.example.library_management.dto.response.borrow.BorrowResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Book;
import com.example.library_management.model.Borrow;
import com.example.library_management.model.Policy;
import com.example.library_management.model.User;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.repository.PolicyRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.service.mail.BorrowMailService;
import com.example.library_management.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CreateBorrowService {
    @Autowired
    private AuditLogger logger;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private BorrowMailService borrowMailService;

    @Transactional
    public BorrowResponse createBorrow(BorrowRequest request){
        Optional<Borrow> existing = borrowRepository.findByUserIdAndBookIdAndIsActive(request.getUserId(), request.getBookId(), true);
        if (existing.isPresent()) {
            String message = messageSource.getMessage("error.borrow.already.existed",null, LocaleContextHolder.getLocale());
            logger.warn("Unable to create borrow for user ID #{} with book ID #{}: {}", request.getUserId(), request.getBookId(), message);
            throw new RuntimeException(message);
        }

        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.user.id.not.found", null, LocaleContextHolder.getLocale())));
        Borrow newBorrow = new Borrow();
        Book book = bookRepository.findByIdForUpdate(request.getBookId()).orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.book.id.not.found", null, LocaleContextHolder.getLocale())));;
        if(book.getCopies() == 0){
            String message = messageSource.getMessage("error.book.is.not.available", null, LocaleContextHolder.getLocale());
            logger.warn("Unable to create borrow for user ID #{} with book ID #{}: {}", request.getUserId(), request.getBookId(), message);
            throw new ApiException(HttpStatus.BAD_REQUEST, "BOOk-NOT-AVAILABLE", message);
        }

        book.setCopies(book.getCopies() - 1);

        Policy borrowDuration = policyRepository.findByKey("borrow_duration").orElseThrow(()->new RuntimeException(messageSource.getMessage("error.policy.not.found",null, LocaleContextHolder.getLocale())));

        newBorrow.setUser(user);
        newBorrow.setBook(book);

        int days = Integer.parseInt(borrowDuration.getValue());
        newBorrow.setDueDate(LocalDateTime.now().plusDays(days));

        borrowRepository.save(newBorrow);
        Book savedBook = bookRepository.save(book);
        try {
            borrowMailService.sendBorrowCreatedEmail(newBorrow);
        } catch (Exception e) {
            logger.error("Failed to send borrow confirmation email for borrow ID #{}: {}", newBorrow.getId(), e.getMessage());
        }
        logger.log("Created borrow for user @{} ID #{} with book {} ID#{}", user.getUsername(), user.getId(), book.getTitle(), book.getId());
        return new BorrowResponse(newBorrow);
    }
}
