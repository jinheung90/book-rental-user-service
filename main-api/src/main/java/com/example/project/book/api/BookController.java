package com.example.project.book.api;



import com.example.project.address.RoadAddress;
import com.example.project.book.client.dto.NaverBookSearchDto;
import com.example.project.book.client.dto.NaverDetailBookDto;
import com.example.project.book.dto.SearchAddressDto;
import com.example.project.book.dto.UserBookLikeDto;
import com.example.project.book.search.service.BookSearchService;
import com.example.project.book.store.entity.UserBook;
import com.example.project.book.store.entity.UserBookLike;

import com.example.project.common.enums.BookSellType;
import com.example.project.common.enums.BookSortType;
import com.example.project.user.client.api.KakaoAddressSearchClient;
import com.example.project.user.client.dto.KakaoAddressSearchDto;
import com.example.project.user.dto.UserProfileDto;

import com.example.project.user.security.CustomUserDetail;
import com.example.project.user.service.UserService;
import com.example.project.book.client.api.NaverBookSearchClient;

import com.example.project.book.dto.SearchBookDto;
import com.example.project.book.dto.UserBookDto;
import com.example.project.book.store.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Slf4j
public class BookController {

    private final BookService bookService;
    private final NaverBookSearchClient naverBookSearchClient;
    private final KakaoAddressSearchClient kakaoAddressSearchClient;

    private final UserService userService;
    private final BookSearchService bookSearchService;


    @GetMapping("/book/search")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(description = "유저 책 검색")
    public ResponseEntity<Page<SearchBookDto>> searchBooks(
            @Parameter(description = "페이지")
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @Parameter(description = "사이즈")
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @Parameter(description = "정렬 키워드")
            @RequestParam(name = "sortKey", defaultValue = "updatedAt", required = false) BookSortType sortKey,
            @Parameter(description = "이름 (나중에는 키워드 검색 예정)")
            @RequestParam(name = "name", required = false) String name,
            @Parameter(description = "유저 아이디")
            @RequestParam(name = "userId", required = false) Long userId,
            @Parameter(description = "판매 가능 상태")
            @RequestParam(name = "bookSellType", required = false, defaultValue = "BOTH") BookSellType bookSellType,
            @Parameter(description = "현재 위치 좌표 x")
            @RequestParam(name = "pos_x") double x,
            @Parameter(description = "현재 위치 좌표 y")
            @RequestParam(name = "pos_y") double y,
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {
        // TODO 검색 엔진으로 변경
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserBookDto> searchResult = null;

        try {
            searchResult = bookSearchService.searchUserBooks(name, sortKey, userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            searchResult = bookService.searchUserBooks(pageRequest, name, userId, customUserDetail.getPK(), bookSellType, sortKey);
        }

        final List<Long> userIds = searchResult.getContent().stream().map(UserBookDto::getUserId).toList();
        final Map<Long, UserProfileDto> userProfileDtoMap = userService.getUserProfilesByUserIds(userIds);

        List<SearchBookDto> resultDto = searchResult.getContent().stream()
                .map(userBookDto -> new SearchBookDto(userBookDto, userProfileDtoMap.get(userBookDto.getUserId())))
                .toList();

        return ResponseEntity.ok(new PageImpl<>(resultDto, pageRequest, searchResult.getTotalElements()));
    }

    @PutMapping("/book/{id}/like")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserBookLikeDto> setBookLike(
            @PathVariable(name = "id") Long userBookId,
            @AuthenticationPrincipal CustomUserDetail customUserDetail) {
        UserBookLike userBookLike = bookService.updateUserBookLike(customUserDetail.getPK(), userBookId);
        bookSearchService.updateBookLikeCount(userBookId, userBookLike.isActivity());
        return ResponseEntity.ok(new UserBookLikeDto(userBookLike.getUserId(), userBookLike.getUserBook().getId(), userBookLike.isActivity(), userBookLike.getUpdatedAt()));
    }

    @GetMapping("/book/wish")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<SearchBookDto>> getWishList(
            @Parameter(description = "페이지")
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @Parameter(description = "사이즈")
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        final Page<UserBookDto> userBookDtos = bookService.getPageWishUserBook(pageRequest);
        final List<Long> userIds = userBookDtos.getContent().stream().map(UserBookDto::getUserId).toList();
        final Map<Long, UserProfileDto> userProfileDtoMap = userService.getUserProfilesByUserIds(userIds);
        final List<SearchBookDto> searchBookDtos = userBookDtos.getContent().stream().map(userBookDto ->
             new SearchBookDto(userBookDto, userProfileDtoMap.get(userBookDto.getUserId()))
        ).toList();
        return ResponseEntity.ok(new PageImpl<>(searchBookDtos, pageRequest, userBookDtos.getTotalElements()));
    }

    @PostMapping("/book")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserBookDto> uploadUserBook(
            @RequestBody UserBookDto userBookDto,
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {
        SearchAddressDto addressDto = userBookDto.getAddress();
        final KakaoAddressSearchDto.Documents document = this.kakaoAddressSearchClient.findOneByNameAndZoneNo(addressDto.getAddressName(), addressDto.getZoneNo());
        final NaverDetailBookDto bookDto = naverBookSearchClient.searchBookByIsbn(userBookDto.getBookInfo().getIsbn());
        final UserBook userBook = bookService.registerUserBook(userBookDto, bookDto, customUserDetail.getPK(), addressDto);
        bookSearchService.saveUserBook(userBookDto, userBook.getUserId(), bookDto, customUserDetail.getPK(), addressDto);
        return ResponseEntity.ok(UserBookDto.fromEntity(userBook));
    }

    @PutMapping("/book/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserBookDto> update(
            @RequestBody UserBookDto userBookDto,
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable(name = "id") Long userBookId
    ) {
        SearchAddressDto addressDto = userBookDto.getAddress();
        if(addressDto.getAddressName() != null && !addressDto.getAddressName().isBlank()) {

        }
        final UserBook userBook = bookService.updateUserBook(userBookDto, customUserDetail.getPK(), userBookId);
        bookSearchService.updateUserBook(userBook.getId(), userBookDto);
        return ResponseEntity.ok(UserBookDto.fromEntity(userBook));
    }

    @GetMapping("/book/search/naver")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<NaverBookSearchDto> searchBooksFromNaver(
            @RequestParam(name = "start", defaultValue = "1") int start,
            @RequestParam(name = "display", defaultValue = "10") int display,
            @RequestParam(name = "name") String name
    ){
        final NaverBookSearchDto result = naverBookSearchClient.getBooksFromName(start, display, name);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/book/image/url")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> getPresignedUrl(
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {
        return ResponseEntity.ok(this.bookService.getUserBookImagePreSignedUrl(customUserDetail.getPK().toString()));
    }

    @GetMapping("/book/naver")
    public ResponseEntity<NaverBookSearchDto.Item> getStoredNaverBook(
            @PathVariable(name = "id") Long bookId
    ) {
        return ResponseEntity.ok(
            NaverBookSearchDto.Item.fromBook(this.bookService.findBookById(bookId))
        );
    }

    @GetMapping("/book/{id}/detail")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserBookDto> getUserBookById(
            @PathVariable(name = "id") Long userBookId,
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {
        bookSearchService.saveUserClick(userBookId, customUserDetail.getPK());
        return ResponseEntity.ok(UserBookDto.fromEntity(this.bookService.findUserBookById(userBookId)));
    }
}
