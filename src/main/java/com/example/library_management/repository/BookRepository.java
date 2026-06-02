package com.example.library_management.repository;

import com.example.library_management.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByTitle(String title);
    List<Book> findByGenres_Id(long genreId);
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
}
