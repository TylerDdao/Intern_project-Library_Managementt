package com.example.library_management.service.borrow;

import com.example.library_management.dto.request.BorrowRequest;
import com.example.library_management.dto.response.BorrowResponse;
import com.example.library_management.model.Book;
import com.example.library_management.model.Borrow;
import com.example.library_management.model.User;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

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

    public BorrowResponse createBorrow(BorrowRequest request){
        Optional<Borrow> existing = borrowRepository.findByUserIdAndBookId(request.getUserId(), request.getBookId());

        if (existing.isPresent()) {
            Borrow borrow = existing.get();
            borrow.setIsActive(false);
            return new BorrowResponse(borrow);
        }

        Borrow newBorrow = new Borrow();
        User user = userRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.user.id.not.found", null, LocaleContextHolder.getLocale())));
        Book book = bookRepository.findById(request.getBookId()).orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.book.id.not.found", null, LocaleContextHolder.getLocale())));;
        if(book.getCopies() == 0){
            newBorrow.setIsActive(false);
            return new BorrowResponse(newBorrow);
        }

        newBorrow.setUser(user);
        newBorrow.setBook(book);
        newBorrow.setDueDate(request.getDueDate());
        borrowRepository.save(newBorrow);
        return new BorrowResponse(newBorrow);
    }
}
