package com.example.library_management.service.borrow;

import com.example.library_management.dto.request.BorrowRequest;
import com.example.library_management.dto.response.BorrowResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Book;
import com.example.library_management.model.Borrow;
import com.example.library_management.model.Policy;
import com.example.library_management.model.User;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.repository.PolicyRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.service.MailService;
import com.example.library_management.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private MailService mailService;

    @Transactional
//    @CacheEvict(value = "books", key = "#request.id")
    public BorrowResponse createBorrow(BorrowRequest request){
        Optional<Borrow> existing = borrowRepository.findByUserIdAndBookIdAndIsActive(request.getUserId(), request.getBookId(), true);
        if (existing.isPresent()) {
            throw new RuntimeException(messageSource.getMessage("error.borrow.already.existed",null, LocaleContextHolder.getLocale()));
        }

        Borrow newBorrow = new Borrow();
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.user.id.not.found", null, LocaleContextHolder.getLocale())));
        Book book = bookRepository.findByIdForUpdate(request.getBookId()).orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.book.id.not.found", null, LocaleContextHolder.getLocale())));;
        if(book.getCopies() == 0){
            throw new RuntimeException(messageSource.getMessage("error.book.is.not.available", null, LocaleContextHolder.getLocale()));
        }

        book.setCopies(book.getCopies() - 1);

        Policy borrowDuration = policyRepository.findByKey("borrow_duration").orElseThrow(()->new RuntimeException(messageSource.getMessage("error.policy.not.found",null, LocaleContextHolder.getLocale())));

        newBorrow.setUser(user);
        newBorrow.setBook(book);

        int days = Integer.parseInt(borrowDuration.getValue());
        newBorrow.setDueDate(LocalDateTime.now().plusDays(days));

        borrowRepository.save(newBorrow);
        bookRepository.save(book);
        mailService.sendBorrowCreatedEmail(newBorrow);
        return new BorrowResponse(newBorrow);
    }
}
