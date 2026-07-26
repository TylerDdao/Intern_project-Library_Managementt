package com.example.library_management.service.borrow;

import com.example.library_management.dto.request.BorrowRequest;
import com.example.library_management.dto.response.BorrowResponse;
import com.example.library_management.dto.response.RoleResponse;
import com.example.library_management.model.Book;
import com.example.library_management.model.Borrow;
import com.example.library_management.model.Feature;
import com.example.library_management.model.Role;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.util.AuditLogger;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    private AuditLogger logger;

    @Transactional
    public BorrowResponse returnBorrow(BorrowRequest request){
        Borrow borrow = borrowRepository.findById(request.getId()).orElseThrow(()-> new RuntimeException(messageSource.getMessage("error.borrow.not.found", null, LocaleContextHolder.getLocale())));

        borrow.setIsActive(request.getIsActive());
        Book book = bookRepository.findById(borrow.getBook().getId()).orElseThrow(() ->new RuntimeException(messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));
        book.setCopies(book.getCopies() + 1);
        borrowRepository.save(borrow);
        bookRepository.save(book);

        logger.log("Returned borrow ID #{}", borrow.getId());
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
