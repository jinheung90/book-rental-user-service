package com.example.project.book.store.service;

import com.example.project.book.client.dto.NaverBookItem;
import com.example.project.book.client.dto.NaverBookSearchDto;
import com.example.project.book.client.dto.NaverDetailBookDto;
import com.example.project.book.dto.SearchAddressDto;
import com.example.project.book.dto.UserBookClickCountDto;
import com.example.project.book.dto.UserBookDto;


import com.example.project.book.dto.UserBookImageDto;
import com.example.project.book.store.repository.*;

import com.example.project.book.store.entity.*;
import com.example.project.common.aws.s3.BucketType;
import com.example.project.common.aws.s3.S3Uploader;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import com.example.project.common.enums.BookSortType;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


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
    private final UserBookLikeRepository userBookLikeRepository;
    private final UserBookAddressRepository userBookAddressRepository;

    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public Page<UserBookDto> searchUserBooks(PageRequest pageRequest, String name, Long searchUserId, Long myUserId, BookSellType bookSellType, BookSortType bookSortType) {
        List<UserBook> userBooks = userBookQuery.searchUserBook(pageRequest, name, searchUserId, bookSellType, bookSortType);
        Map<Long, UserBookLike> likeMap = this.getBookLikesByIdInAndUserId(userBooks.stream().map(UserBook::getId).toList(), myUserId);
        List<UserBookDto> userBookDtos = userBooks.stream().map(
                userBook -> UserBookDto.whenSearch(userBook, likeMap.get(userBook.getId()))
        ).toList();
        return new PageImpl<>(userBookDtos, pageRequest, userBookQuery.countSearchUserBook(name, searchUserId, bookSellType, bookSortType));
    }

    @Transactional
    public UserBook registerUserBook(
            UserBookDto userBookDto,
            NaverBookItem item,
            Long userId,
            SearchAddressDto addressDto
    ) {
        Book book = this.findBookByIsbnOrElseSave(item);
        checkMainImageCount(userBookDto.getUserBookImageDtos());
        return saveUserBook(userBookDto, book, userId, addressDto);
    }

    @Transactional
    public UserBook updateUserBook(
            UserBookDto userBookDto,
            Long userId,
            Long userBookId,
            SearchAddressDto addressDto,
            NaverBookItem naverBookItem
    ) {
         final UserBook userBook = userBookRepository.findByUserIdAndId(userId, userBookId)
                 .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "can't access userBook"));

         userBook.setBookSellType(userBookDto.getBookSellType());
         userBook.setDetail(userBookDto.getDetail());
         userBook.setTitle(userBookDto.getTitle());
         userBook.setRentState(userBookDto.getRentState());
         userBook.setRentPrice(userBookDto.getRentPrice());
         userBook.setSellPrice(userBookDto.getSellPrice());
         userBook.setImages(updateImages(userBookDto.getUserBookImageDtos(), userBook));

        if(addressDto != null && addressDto.getAddressName() != null && !addressDto.getAddressName().isBlank()) {
            Long preUserBookId = userBook.getUserBookAddress().getId();
            userBook.setUserBookAddress(SearchAddressDto.toEntity(addressDto));
            this.deleteUserBookAddressById(preUserBookId);
        }

        if(naverBookItem != null) {
            Book book = findBookByIsbnOrElseSave(naverBookItem);
            userBook.setBook(book);
        }

        return userBook;
    }

    @Transactional
    public void deleteUserBookAddressById(Long id) {
        userBookAddressRepository.deleteById(id);
    }

    @Transactional
    public List<UserBookImage> updateImages(List<UserBookImageDto> userBookImages, UserBook userBook) {
        checkMainImageCount(userBookImages);
        this.deleteUserBookImages(userBook.getImages());
        return this.saveUserBookImages(userBookImages, userBook);
    }

    public void checkMainImageCount(List<UserBookImageDto> userBookImages) {
        int count = 0;
        for (UserBookImageDto image : userBookImages) {
            if(image.getMainImage()) {
                count++;
            }
        }
        if(count != 1) {
            log.warn(String.format("main image is not 1, count: %d", count));
        }
    }

    @Transactional
    public void deleteUserBookImages(List<UserBookImage> images) {
        if(images != null && !images.isEmpty()) {
            for (UserBookImage image : images) {
                deleteUserBookS3ImageByUrl(image.getImageUrl());
            }
            userBookImageRepository.deleteAll(images);
        }
    }

    @Transactional
    public List<UserBookImage> saveUserBookImages(List<UserBookImageDto> userBookImages, UserBook userBook) {
        if(userBookImages != null) {
            return userBookImageRepository.saveAll(userBookImages.stream().map(userBookImageDto ->
                    UserBookImageDto.toEntity(userBookImageDto, userBook)
            ).toList());
        }
        return new ArrayList<>();
    }

    private void deleteUserBookS3ImageByUrl(String imageUrl) {
        s3Uploader.deleteByKey(BucketType.BOOK, imageUrl);
    }

    public UserBook saveUserBook(UserBookDto userBookDto, Book book, Long userId, SearchAddressDto addressDto) {
        return userBookRepository.save(
            UserBook.builder()
                .rentPrice(userBookDto.getRentPrice())
                .bookSellType(userBookDto.getBookSellType())
                .sellPrice(userBookDto.getSellPrice())
                .rentState(BookRentalStateType.AVAILABLE)
                .bookSellType(BookSellType.BOTH)
                .userBookAddress(UserBookAddress.builder()
                    .addressName(addressDto.getAddressName())
                    .zoneNo(addressDto.getZoneNo())
                    .longitude(addressDto.getLongitude())
                    .latitude(addressDto.getLatitude())
                    .build())
                .images(userBookDto.getUserBookImageDtos().stream().map(
                    userBookImageDto -> UserBookImage
                        .builder()
                        .imageUrl(userBookImageDto.getImageUrl())
                        .imageOrder(userBookImageDto.getImageOrder())
                        .mainImage(userBookImageDto.getMainImage())
                        .build()
                ).toList())
                .activity(true)
                .book(book)
                .userId(userId)
                .detail(userBookDto.getDetail())
                .title(userBookDto.getTitle())
                .build()
        );
    }

    public Map<Long, UserBookLike> getBookLikesByIdInAndUserId(List<Long> ids, Long userId) {
        return userBookLikeRepository.findByUserBookIdInAndUserIdAndActivityIsTrue(ids, userId).stream().collect(
                Collectors.toMap(userBookLike -> userBookLike.getUserBook().getId(), userBookLike -> userBookLike));
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

    @Transactional
    public Book findBookByIsbnOrElseSave(NaverBookItem bookDto) {
        log.info(bookDto.getLink());
        Optional<Book> optionalBook = bookRepository.findByIsbn(bookDto.getIsbn());
        return optionalBook.orElseGet(() -> bookRepository.save(NaverBookItem.toEntity(bookDto)));
    }

    @Transactional
    public List<Book> saveAllBooks(List<NaverBookItem> items) {
        return bookRepository.saveAll(
            items.stream().map(
                    NaverBookItem::toEntity
            ).toList()
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

    public String getUserBookImagePreSignedUrl(String userId) {
        return s3Uploader.createUserBookImagePreSignedUrl(userId + "/" + UUID.randomUUID());
    }

    @Transactional
    public UserBookLike updateUserBookLike(Long userId, Long userBookId) {
        Optional<UserBookLike> optionalUserBookLike = userBookLikeRepository.findByUserIdAndUserBookId(userId, userBookId);
        if(optionalUserBookLike.isPresent()) {
            final UserBookLike userBookLike = optionalUserBookLike.get();
            userBookLike.changeActivity();
            return userBookLike;
        } else {
            return userBookLikeRepository.save(
                UserBookLike
                    .builder()
                    .userId(userId)
                    .userBook(
                        findUserBookById(userBookId)
                    )
                    .activity(true)
                .build()
            );
        }
    }

    @Transactional(readOnly = true)
    public UserBook findUserBookById(Long id) {
        return userBookRepository.findById(id).orElseThrow(
            () -> new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "not exists userbook")
        );
    }

    @Transactional(readOnly = true)
    public Book findBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "not exists userbook")
        );
    }

    @Transactional(readOnly = true)
    public Page<UserBookDto> getPageWishUserBook(PageRequest pageRequest) {
        final List<UserBookDto> userBooks = userBookQuery.getWishList(pageRequest).stream().map(UserBookDto::fromEntity).toList();
        final Long count = userBookQuery.countWishList();
        return new PageImpl<>(userBooks, pageRequest, count);
    }

    @Transactional(readOnly = true)
    public List<Book> findBookAll() {
        return bookRepository.findAll();
    }

}
