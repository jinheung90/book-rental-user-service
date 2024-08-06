package com.example.project.book.service;

import com.example.project.book.dto.UserBookDto;

import com.example.project.book.entity.BookLike;
import com.example.project.book.repository.UserBookQuery;
import com.example.project.book.repository.UserBookRepository;

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

    public Page<UserBookDto> pageBooks(PageRequest pageRequest, String name, Long userId) {
        List<UserBookDto> userBooks = userBookQuery.searchUserBook(pageRequest, name, userId);
        return new PageImpl<>(userBooks, pageRequest, userBookQuery.countSearchUserBook(name, userId));
    }
}
