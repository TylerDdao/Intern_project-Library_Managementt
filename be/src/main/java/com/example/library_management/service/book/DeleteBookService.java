package com.example.library_management.service.book;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeleteBookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public String deleteBook(BookRequest request){
        Book book = bookRepository.findByTitle(request.getTitle())
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));
        bookRepository.delete(book);
        String message = messageSource.getMessage("book.delete", null, LocaleContextHolder.getLocale());
        String author = messageSource.getMessage("book.author", null, LocaleContextHolder.getLocale());
        String title = messageSource.getMessage("book.title", null, LocaleContextHolder.getLocale());
        logger.log("Deleted book ID #{} | Title: {} | Author: {}", book.getId(), book.getTitle(), book.getAuthor());
        return  message + " ID#"+ book.getId() + " | " + title + ": " + book.getTitle() + " | " + author + ": " + book.getAuthor();
    }
}
