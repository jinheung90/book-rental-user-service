package com.example.project.book.api;


import com.example.project.book.dto.UserBookDto;
import com.example.project.book.service.BookService;
import com.example.project.jpa.RestPage;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<Page<UserBookDto>> getAllBooks(
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    ) {
        return ResponseEntity.ok(bookService.pageBooks(page, size));
    }
}
