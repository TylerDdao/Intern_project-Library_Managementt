package com.example.library_management.service.book;

import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Book;
import com.example.library_management.model.Borrow;
import com.example.library_management.model.Post;
import com.example.library_management.repository.*;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private CommentRepository commentRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private void deleteBookCover(Book book) {
        if (book.getCoverUrl() == null || book.getCoverUrl().isBlank()) {
            return;
        }

        try {
            Path filePath = Paths.get(uploadDir, book.getCoverUrl());
            Files.deleteIfExists(filePath);

            logger.log("Deleted book cover {} of book {}, ID #{}", filePath, book.getTitle(), book.getId());
        } catch (IOException e) {
            log.error("Failed to delete book cover {} of book {}, ID #{}: {}", book.getCoverUrl(), book.getTitle(), book.getId(), e.getMessage());
        }
    }

    @Transactional
    public String deleteBook(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));

        if (borrowRepository.existsByBook_IdAndIsActiveTrue(id)){
            String message = messageSource.getMessage("error.there.are.borrows.of.this.book", null, LocaleContextHolder.getLocale());
            log.warn("Failed to delete book {}, ID #{}: {}", book.getTitle(), book.getId(), message);
            throw new ApiException(HttpStatus.BAD_REQUEST, "BORROWS-EXIST", message);
        }

        List<Post> posts = postRepository.findByBook_Id(id);
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        commentRepository.deleteAllByPostIn(posts);
        postLikeRepository.deleteAllByPost_IdIn(postIds);
        postRepository.deleteAll(posts);
        logger.log("Deleted {} posts that are associated with book {}, ID #{}", posts.size(), book.getTitle(), book.getId());

        List<Borrow> borrows = borrowRepository.findByBook_Id(id);
        borrowRepository.deleteAll(borrows);
        logger.log("Deleted {} inactive borrows that are associated with book {}, ID #{}",borrows.size(), book.getTitle(), book.getId());

        bookRepository.delete(book);
        deleteBookCover(book);
        String message = messageSource.getMessage("book.delete", null, LocaleContextHolder.getLocale());
        String author = messageSource.getMessage("book.author", null, LocaleContextHolder.getLocale());
        String title = messageSource.getMessage("book.title", null, LocaleContextHolder.getLocale());
        logger.log("Deleted book title {}, author {}, ID #{}", book.getTitle(), book.getAuthor(), book.getId());
        return  message + " ID#"+ book.getId() + " | " + title + ": " + book.getTitle() + " | " + author + ": " + book.getAuthor();
    }
}
