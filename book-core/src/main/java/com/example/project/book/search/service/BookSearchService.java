package com.example.project.book.search.service;


import com.example.project.book.client.dto.NaverDetailBookDto;
import com.example.project.book.dto.UserBookDto;

import com.example.project.book.search.doc.Book;
import com.example.project.book.search.doc.UserBook;
import com.example.project.book.search.repository.UserBookESRepository;

import com.example.project.common.util.JamoSeparate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final UserBookESRepository userBookESRepository;

    public void saveUserBook(
            UserBookDto userBookDto,
            Long userBookId,
            NaverDetailBookDto detailBookDto,
            Long userId
    ) {
        userBookESRepository.save(
            UserBook.builder()
                .bookId(userBookId)
                .bookSellType(userBookDto.getBookSellType())
                .activity(true)
                .book(Book.fromBook(detailBookDto.getChannel().getItem()))
                .rentPrice(userBookDto.getRentPrice())
                .sellPrice(userBookDto.getSellPrice())
                .title(userBookDto.getTitle())
                .imageUrls(userBookDto.getUserBookImageDtos())
                .userId(userId)
                .likeCount(0L)
                .titleWordUnits(JamoSeparate.separate(userBookDto.getTitle()))
                .build()
        );
    }

    public Page<UserBookDto> searchUserBooks() {
        return null;
    }
}
