package com.example.project.book.service;

import com.example.project.book.dto.UserBookDto;

import com.example.project.book.repository.UserBookQuery;
import com.example.project.book.repository.UserBookRepository;
import com.example.project.jpa.RestPage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BookService {

    private final UserBookRepository usedBookRepository;
    private final UserBookQuery userBookQuery;


    public Page<UserBookDto> pageBooks(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<UserBookDto> userBooks = userBookQuery.findAllUserBook(pageable, "");
        return new PageImpl<>(userBooks,pageable, userBookQuery.countUserBook());
    }
}
