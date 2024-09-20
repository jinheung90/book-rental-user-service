package com.example.project.book.store.service;

import com.example.project.book.client.dto.NaverBookItem;
import com.example.project.book.client.dto.NaverBookSearchDto;
import com.example.project.book.client.dto.NaverDetailBookDto;
import com.example.project.book.dto.*;


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
    public Page<UserBookDto> searchUserBooks(PageRequest pageRequest, String name, Long searchUserId, BookSellType bookSellType, BookSortType bookSortType, Double latitude, Double longitude) {
        List<UserBook> userBooks = userBookQuery.searchUserBook(pageRequest, name, searchUserId, bookSellType, bookSortType, latitude, longitude);
        List<UserBookDto> userBookDtos = userBooks.stream().map(
                UserBookDto::whenSearch
        ).toList();
        return new PageImpl<>(userBookDtos, pageRequest, userBookQuery.countSearchUserBook(name, searchUserId, bookSellType, bookSortType, latitude, longitude));
    }

    @Transactional
    public UserBook registerUserBook(
            UserBookRequest request,
            NaverBookItem item,
            Long userId,
            SearchAddressDto addressDto
    ) {
        Book book = this.findBookByIsbnOrElseSave(item);
        checkMainImageCount(request.getUserBookImageDtos());
        return saveUserBook(request, book, userId, addressDto);
    }

    @Transactional
    public UserBook updateUserBook(
            UserBookRequest request,
            Long userId,
            Long userBookId,
            SearchAddressDto addressDto,
            NaverBookItem naverBookItem
    ) {
         final UserBook userBook = userBookRepository.findByUserIdAndId(userId, userBookId)
                 .orElseThrow(() -> new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "can't access userBook"));

         userBook.setBookSellType(request.getBookSellType(), request.getRentPrice(), userBook.getSellPrice());
         userBook.setDetail(request.getDetail());
         userBook.setTitle(request.getTitle());
         userBook.setRentState(request.getRentState());
         userBook.setRentPrice(request.getRentPrice());
         userBook.setSellPrice(request.getSellPrice());
         userBook.setImages(updateImages(request.getUserBookImageDtos(), userBook));

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

    @Transactional(readOnly = true)
    public Map<Long, List<UserBookImage>> getUserBookImageUserBookIdIn(List<Long> ids) {
        Map<Long, List<UserBookImage>> result = new HashMap<>();
        this.userBookImageRepository.findAllByUserBookIdInOrderByImageOrder(ids).forEach(
                        userBookImage -> {
                            Long userBookId = userBookImage.getUserBook().getId();
                            List<UserBookImage> images = result.get(userBookId);
                            if(images == null) {
                                result.put(userBookId, new ArrayList<>() {{
                                    add(userBookImage);
                                }});
                            } else {
                                images.add(userBookImage);
                            }
                        }
                );
        return result;
    }

    @Transactional(readOnly = true)
    public List<UserBookDto> addUserBookInfo(List<UserBookDto> userBookDtos, Long userId) {
        List<Long> userBookIds = userBookDtos.stream().map(UserBookDto::getId).toList();

        Map<Long, List<UserBookImage>> userBookImageMap = this.getUserBookImageUserBookIdIn(userBookIds);
        Map<Long, UserBookLike> userBookLikeMap = this.getBookLikesByIdInAndUserId(userBookIds, userId);
        userBookDtos.forEach(
                userBookDto -> {
                    userBookDto.setBookLikeState(userBookLikeMap.get(userBookDto.getId()));
                    userBookDto.setUserBookImage(userBookImageMap.get(userBookDto.getId()));
                }
        );
        return userBookDtos;
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
        if(userBookImages != null && !userBookImages.isEmpty()) {
            return userBookImageRepository.saveAll(userBookImages.stream().map(userBookImageDto ->
                    UserBookImageDto.toEntity(userBookImageDto, userBook)
            ).toList());
        }
        return new ArrayList<>();
    }

    private void deleteUserBookS3ImageByUrl(String imageUrl) {
        s3Uploader.deleteByKey(BucketType.BOOK, imageUrl);
    }

    @Transactional
    public UserBook saveUserBook(UserBookRequest request, Book book, Long userId, SearchAddressDto addressDto) {
        UserBook userBook = userBookRepository.save(
            UserBookDto.toEntity(request, book, userId, addressDto)
        );
        List<UserBookImage> userBookImages = saveUserBookImages(request.getUserBookImageDtos(), userBook);
        userBook.setImages(userBookImages);
        return userBook;
    }



    @Transactional(readOnly = true)
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
