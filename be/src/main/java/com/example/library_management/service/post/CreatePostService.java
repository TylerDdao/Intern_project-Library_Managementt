package com.example.library_management.service.post;

import com.example.library_management.dto.request.post.PostRequest;
import com.example.library_management.dto.response.post.PostResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Book;
import com.example.library_management.model.Post;
import com.example.library_management.repository.BookRepository;
import com.example.library_management.repository.PostRepository;
import com.example.library_management.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CreatePostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public PostResponse createPost(PostRequest request){
        Post post = new Post();
        Book book = bookRepository.findById(request.getBook()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND, "BOOK-NOT-FOUND", messageSource.getMessage("error.book.not.found", null, LocaleContextHolder.getLocale())));
        post.setSubject(request.getSubject());
        post.setContent(request.getContent());
        post.setBook(book);
        Post savedPost = postRepository.save(post);
        logger.log("Created post ID#{}", savedPost.getId());
        return new PostResponse(savedPost, false, false);
    }
}
