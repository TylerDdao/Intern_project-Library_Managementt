package com.example.library_management.service.book;

import com.example.library_management.dto.request.BookRequest;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Book;
import com.example.library_management.model.Post;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.repository.PostRepository;
import com.example.library_management.util.AuditLogger;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DeleteBookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    @Transactional
    public String deleteBook(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));

        if (borrowRepository.existsByBook_Id(id)){
            throw new ApiException(HttpStatus.BAD_REQUEST, "BORROWS-EXIST",messageSource.getMessage("error.there.are.borrows.of.this.book", null, LocaleContextHolder.getLocale()));
        }

        List<Post> posts = postRepository.findByBook_Id(id);

        postRepository.deleteAll(posts);
        logger.log("Deleted all posts that are associated with book {}, ID #{}", book.getTitle(), book.getId());

        bookRepository.delete(book);
        String message = messageSource.getMessage("book.delete", null, LocaleContextHolder.getLocale());
        String author = messageSource.getMessage("book.author", null, LocaleContextHolder.getLocale());
        String title = messageSource.getMessage("book.title", null, LocaleContextHolder.getLocale());
        logger.log("Deleted book ID #{} | Title: {} | Author: {}", book.getId(), book.getTitle(), book.getAuthor());
        return  message + " ID#"+ book.getId() + " | " + title + ": " + book.getTitle() + " | " + author + ": " + book.getAuthor();
    }
}
