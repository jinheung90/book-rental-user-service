package com.example.project.book.service;

import com.example.project.book.dto.UserBookDto;


import com.example.project.book.entity.UserBook;
import com.example.project.book.repository.UserBookQuery;
import com.example.project.book.repository.UserBookRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
@RequiredArgsConstructor
public class BookService {

    private final UserBookRepository usedBookRepository;
    private final UserBookQuery userBookQuery;

    @Transactional(readOnly = true)
    public Page<UserBookDto> pageBooks(PageRequest pageRequest, String name, Long userId) {
        List<UserBook> userBooks = userBookQuery.searchUserBook(pageRequest, name, userId);
        List<UserBookDto> userBookDtos = userBooks.stream().map(UserBookDto::fromEntity).toList();
        return new PageImpl<>(userBookDtos, pageRequest, userBookQuery.countSearchUserBook(name, userId));
    }

    @Transactional
    public void inactiveUserBooks(Long userId) {
        List<UserBook> userBooks = findAllByUser(userId);
        userBooks.forEach(UserBook::inactive);
    }

    public List<UserBook> findAllByUser(Long userId) {
        return usedBookRepository.findAllByUserId(userId);
    }

}
