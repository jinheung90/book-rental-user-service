package com.example.project.book.service;

import com.example.project.book.client.dto.NaverDetailBookDto;
import com.example.project.book.dto.UserBookDto;


import com.example.project.book.entity.*;
import com.example.project.book.repository.*;

import com.example.project.common.aws.s3.BucketType;
import com.example.project.common.aws.s3.S3Uploader;
import com.example.project.common.enums.BookRentalStateType;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final UserBookRepository userBookRepository;
    private final UserBookQuery userBookQuery;
    private final BookRepository bookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final UserBookCategoryRepository userBookCategoryRepository;
    private final UserBookImageRepository userBookImageRepository;
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public Page<UserBookDto> searchUserBooks(PageRequest pageRequest, String name, Long userId) {
        List<UserBook> userBooks = userBookQuery.searchUserBook(pageRequest, name, userId);
        List<UserBookDto> userBookDtos = userBooks.stream().map(UserBookDto::fromEntity).toList();
        return new PageImpl<>(userBookDtos, pageRequest, userBookQuery.countSearchUserBook(name, userId));
    }

    @Transactional
    public UserBook registerUserBook(
            Long isbn,
            String title,
            String detail,
            BigDecimal price,
            List<Long> categoryIds,
            Long userId,
            NaverDetailBookDto naverDetailBookDto,
            MultipartFile[] files
    ) {
        Book book = this.findBookByIsbnOrElseSave(isbn, naverDetailBookDto);
        UserBook userBook = saveUserBook(title, detail, userId, price, book);
        List<UserBookCategory> userBookCategories = saveUserBookCategory(categoryIds, userBook);
        List<UserBookImage> userBookImages = saveImages(files, userBook);
        return userBook;
    }

    public UserBook saveUserBook(String title, String detail, Long userId, BigDecimal price, Book book) {
        return userBookRepository.save(
            UserBook.builder()
                .price(price)
                .state(BookRentalStateType.AVAILABLE)
                .activity(true)
                .book(book)
                .userId(userId)
                .detail(detail)
                .title(title)
                .build()
        );
    }

    public List<UserBookImage> saveImages(MultipartFile[] files, UserBook userBook) {
        List<String> urls = Arrays.stream(files).map(
            file -> s3Uploader.putUserBookImage(file, BucketType.BOOK, userBook.getId().toString())
        ).toList();
        List<UserBookImage> userBookImages = urls.stream().map(
                url -> UserBookImage.builder()
                        .imageUrl(url)
                        .userBook(userBook)
                        .build()
        ).toList();

        for (int i = 0; i < userBookImages.size(); i++) {
            userBookImages.get(i).setImageOrder(i);

        }

        return userBookImageRepository.saveAll(userBookImages);
    }

    @Transactional
    public List<UserBookCategory> saveUserBookCategory(List<Long> ids, UserBook userBook) {
        if(ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<BookCategory> categories = findCategoryByIds(ids);

        if(ids.size() != categories.size()) {
            log.warn("비 활성화 된 카테고리에 대해 저장 요청 들어옴");
        }

        return userBookCategoryRepository.saveAll(
            categories.stream().map(bookCategory -> UserBookCategory.builder()
                .bookCategory(bookCategory)
                .userBook(userBook)
                .build())
            .toList()
        );
    }

    public List<BookCategory> findCategoryByIds(List<Long> ids) {
        return bookCategoryRepository.findAllByActivityIsTrueAndIdIn(ids);
    }




    public Book findBookByIsbnOrElseSave(Long isbn, NaverDetailBookDto bookDto) {
        NaverDetailBookDto.ChannelItem item = bookDto.getChannel().getItem();
        return bookRepository.findByIsbn(isbn)
            .orElse(
                bookRepository.save(
                    Book.builder()
                        .isbn(item.getIsbn())
                        .author(item.getAuthor())
                        .link(item.getLink())
                        .description(item.getDescription())
                        .discount(item.getDiscount())
                        .pubdate(item.getPubdate())
                        .imageUrl(item.getImage())
                        .publisher(item.getPublisher())
                        .build())
            );
    }

    @Transactional
    public void inactiveUserBooks(Long userId) {
        List<UserBook> userBooks = findAllByUser(userId);
        userBooks.forEach(UserBook::inactive);
    }

    public List<UserBook> findAllByUser(Long userId) {
        return userBookRepository.findAllByUserId(userId);
    }

}
