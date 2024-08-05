package com.example.project.book.api;


import com.example.project.book.dto.UserBookDto;
import com.example.project.book.service.BookService;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<Page<UserBookDto>> getAllBooks(
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size,
            @RequestParam(name = "direction", defaultValue = "DESC", required = false) String direction,
            @RequestParam(name = "sortKey", defaultValue = "updatedAt", required = false) String sortKey,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "userId", required = false) Long userId
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortKey);
        return ResponseEntity.ok(bookService.pageBooks(pageRequest, name, userId));
    }
}
