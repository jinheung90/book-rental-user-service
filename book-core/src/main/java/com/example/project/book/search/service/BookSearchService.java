package com.example.project.book.search.service;


import com.example.project.book.client.dto.NaverBookItem;

import com.example.project.book.dto.SearchAddressDto;
import com.example.project.book.dto.UserBookClickCountDto;
import com.example.project.book.dto.UserBookDto;

import com.example.project.book.dto.UserBookRequest;
import com.example.project.book.search.doc.Book;
import com.example.project.book.search.doc.UserBook;

import com.example.project.book.search.repository.UserBookESQuery;
import com.example.project.book.search.repository.UserBookESRepository;

import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import com.example.project.common.enums.BookSortType;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import com.example.project.common.util.JamoSeparate;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookSearchService {

    private final UserBookESRepository userBookESRepository;
    private final UserBookESQuery userBookESQuery;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Async
    public void saveUserBook(
        UserBookRequest request,
        Long userBookId,
        NaverBookItem bookItem,
        Long userId,
        SearchAddressDto addressDto
    ) {
        userBookESRepository.save(
            UserBook.builder()
                .bookId(userBookId)
                .bookSellType(request.getBookSellType())
                .activity(true)
                .book(Book.fromBook(bookItem))
                .rentState(BookRentalStateType.AVAILABLE)
                .rentPrice(request.getRentPrice().intValue())
                .sellPrice(request.getSellPrice().intValue())
                .title(request.getTitle())
                .detail(request.getDetail())
                .images(request.getUserBookImageDtos())
                .addressId(addressDto.getId())
                .addressName(addressDto.getAddressName())
                .addressZoneNo(addressDto.getZoneNo())
                .location(new GeoPoint(addressDto.getLatitude(), addressDto.getLongitude()))
                .userId(userId)
                .likeCount(0L)
                .build()
        );
    }

    public Page<UserBookDto> searchUserBooks(
        String keyword,
        BookSortType sortType,
        Long userId,
        BookSellType bookSellType,
        Double longitude,
        Double latitude,
        PageRequest pageRequest
    ) {
        SearchHits<UserBook> userBookSearchHits = userBookESQuery.searchUserBookQuery(
                keyword,
                sortType,
                userId,
                bookSellType,
                longitude,
                latitude,
                pageRequest
        );

        List<UserBookDto> userBookDtos = userBookSearchHits.stream().map(
                userBookSearchHit -> UserBookDto.fromDoc(userBookSearchHit.getContent())
        ).toList();
        return new PageImpl<>(userBookDtos, pageRequest, userBookSearchHits.getTotalHits());
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

    // elasticsearch는 업데이트라는 개념이 없으므로 삭제 후 생성
    public void updateUserBook(Long userBookId, UserBookRequest userBookRequest, SearchAddressDto newAddressDto, NaverBookItem book) {

        UserBook userBook = findByBookId(userBookId);
        userBookESRepository.deleteById(userBook.getId());
        userBook.updateUserBook(
                userBookRequest.getTitle(),
                userBookRequest.getBookSellType(),
                userBookRequest.getDetail(),
                Book.fromBook(book),
                userBookRequest.getSellPrice().intValue(),
                userBookRequest.getRentState(),
                userBookRequest.getRentPrice().intValue(),
                userBookRequest.getUserBookImageDtos(),
                newAddressDto
        );
        userBookESRepository.save(userBook);
    }

    @Async
    public void inactiveUserBookDocs(Long userId) {
        List<UserBook> userBookList = userBookESRepository.findByUserId(userId);
        List<String> deleteList = userBookList.stream().map(UserBook::getId).toList();
        userBookList.forEach(UserBook::inactive);
        userBookESRepository.saveAll(userBookList);
        userBookESRepository.deleteAllById(deleteList);
    }
}
