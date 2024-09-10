package com.example.project.book.search.service;


import com.example.project.book.client.dto.NaverBookItem;

import com.example.project.book.dto.SearchAddressDto;
import com.example.project.book.dto.UserBookClickCountDto;
import com.example.project.book.dto.UserBookDto;

import com.example.project.book.search.doc.Book;
import com.example.project.book.search.doc.UserBook;
import com.example.project.book.search.doc.UserBookClickLog;
import com.example.project.book.search.repository.UserBookClickESRepository;
import com.example.project.book.search.repository.UserBookESQuery;
import com.example.project.book.search.repository.UserBookESRepository;

import com.example.project.common.enums.BookSellType;
import com.example.project.common.enums.BookSortType;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.common.util.JamoSeparate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final UserBookESRepository userBookESRepository;
    private final UserBookClickESRepository userBookClickESRepository;
    private final UserBookESQuery userBookESQuery;

    @Async
    public void saveUserBook(
        UserBookDto userBookDto,
        Long userBookId,
        NaverBookItem bookItem,
        Long userId,
        SearchAddressDto addressDto
    ) {
        userBookESRepository.save(
            UserBook.builder()
                .bookId(userBookId)
                .bookSellType(userBookDto.getBookSellType())
                .activity(true)
                .book(Book.fromBook(bookItem))
                .titleWordUnits(JamoSeparate.separate(bookItem.getTitle()))
                .rentPrice(userBookDto.getRentPrice().intValue())
                .sellPrice(userBookDto.getSellPrice().intValue())
                .title(userBookDto.getTitle())
                .images(userBookDto.getUserBookImageDtos())
                .addressId(addressDto.getId())
                .addressName(addressDto.getAddressName())
                .addressZoneNo(addressDto.getZoneNo())
                .location(new GeoPoint(addressDto.getLatitude(), addressDto.getLongitude()))
                .userId(userId)
                .likeCount(0L)
                .titleWordUnits(JamoSeparate.separate(userBookDto.getTitle()))
                .build()
        );
    }

    public Page<UserBookDto> searchUserBooks(
        String keyword,
        BookSortType sortType,
        Long userId,
        BookSellType bookSellType,
        double longitude,
        double latitude,
        PageRequest pageRequest
    ) {
        return userBookESQuery.searchUserBookQuery(
            keyword,
            sortType,
            userId,
            bookSellType,
            longitude,
            latitude,
            pageRequest
        );
    }

    @Async
    public void updateBookLikeCount(Long userBookId, boolean activity) {
        UserBook userBook = this.findByBookId(userBookId);

        if(activity) {
            userBook.increaseLikeCount();
        } else {
            userBook.decreaseLikeCount();
        }

        userBookESRepository.save(userBook);
    }

    public UserBook findByBookId(Long userBookId) {
        return userBookESRepository.findByBookId(userBookId)
            .orElseThrow(
                () -> new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "not exists bookId")
            );
    }

    public void updateUserBook(Long userBookId, UserBookDto userBookDto, SearchAddressDto newAddressDto) {

        UserBook userBook = findByBookId(userBookId);
        userBook.setBookSellType(userBookDto.getBookSellType());
        userBook.setDetail(userBookDto.getDetail());
        userBook.setImages(userBookDto.getUserBookImageDtos());
        userBook.setRentPrice(userBookDto.getRentPrice().intValue());
        userBook.setSellPrice(userBookDto.getSellPrice().intValue());
        userBook.setTitle(userBookDto.getTitle());
        userBook.setRentState(userBookDto.getRentState());
        userBook.setAddress(newAddressDto);

        userBookESRepository.save(userBook);
    }

    @Async
    public void updateUserBookClickCount(Map<Long, UserBookClickCountDto> userBookClickCountDtoMap) {
        List<UserBook> userBooks = userBookESRepository.findByBookIdIn(userBookClickCountDtoMap.keySet());
        userBooks.forEach(userBook -> userBook.setClickCount(userBookClickCountDtoMap.get(userBook.getBookId()).getCount()));
    }

    @Async
    public void saveUserClick(Long userBookId, Long userId) {
        this.userBookClickESRepository.save(
            UserBookClickLog.builder()
                .userBookId(userBookId)
                .userId(userId)
                .createdAt(Instant.now())
                .build()
        );
    }

    public List<UserBookClickLog> getClickLogWithRange(Instant start, Instant end) {
        return this.userBookClickESRepository.findAllByCreatedAtBetween(start, end);
    }
}
