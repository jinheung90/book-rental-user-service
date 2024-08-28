package com.example.project.book.store.repository;

import com.example.project.book.store.entity.Book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(Long isbn);
}
