package com.example.project.book.api;

import com.example.project.book.BookClickService;
import com.example.project.book.client.dto.NaverBookItem;
import com.example.project.book.client.dto.NaverBookSearchDto;
import com.example.project.book.client.dto.NaverDetailBookDto;
import com.example.project.book.dto.*;
import com.example.project.book.search.service.BookSearchService;

import com.example.project.book.store.entity.Book;
import com.example.project.book.store.entity.UserBook;
import com.example.project.book.store.entity.UserBookLike;

import com.example.project.common.enums.BookSellType;
import com.example.project.common.enums.BookSortType;

import com.example.project.common.util.CommonFunction;
import com.example.project.user.client.api.KakaoAddressSearchClient;
import com.example.project.address.dto.KakaoAddressSearchDto;
import com.example.project.user.dto.UserProfileDto;

import com.example.project.user.entity.User;
import com.example.project.user.entity.UserAddress;
import com.example.project.user.security.CustomUserDetail;
import com.example.project.user.service.UserService;
import com.example.project.book.client.api.NaverBookSearchClient;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


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
    private final BookClickService bookClickService;


    @GetMapping("/book/search")
    @Operation(description = "유저 책 검색")
    public ResponseEntity<Page<SearchBookDto>> searchBooks(
            @Parameter(description = "페이지")
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @Parameter(description = "사이즈")
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @Parameter(description = "정렬 키워드")
            @RequestParam(name = "sortKey", defaultValue = "UPDATED_AT", required = false) BookSortType sortKey,
            @Parameter(description = "이름 (나중에는 키워드 검색 예정)")
            @RequestParam(name = "name", required = false) String name,
            @Parameter(description = "유저 아이디")
            @RequestParam(name = "userId", required = false) Long userId,
            @Parameter(description = "판매 가능 상태")
            @RequestParam(name = "bookSellType", required = false, defaultValue = "BOTH") BookSellType bookSellType,
            @Parameter(description = "현재 위치 좌표 (longitude) x")
            @RequestParam(name = "longitude", required = false) Double longitude,
            @Parameter(description = "현재 위치 좌표 (latitude) y")
            @RequestParam(name = "latitude", required = false) Double latitude,
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserBookDto> searchResult;

        try {
            searchResult = bookSearchService.searchUserBooks(name, sortKey, userId, bookSellType, longitude, latitude, pageRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            searchResult = bookService.searchUserBooks(pageRequest, name, userId, bookSellType, sortKey);
        }

        List<UserBookDto> userBookDtos = bookService.addUserBookInfo(searchResult.getContent(), customUserDetail.getPK());

        final List<Long> userIds = searchResult.getContent().stream().map(UserBookDto::getUserId).toList();

        // 유저 프로필
        final Map<Long, UserProfileDto> userProfileDtoMap = userService.getUserProfilesByUserIds(userIds);

        List<SearchBookDto> resultDto = userBookDtos.stream()
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
            @RequestBody UserBookRequest request,
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ) {

        final KakaoAddressSearchDto.RoadAddressDto roadAddressDto =
                this.kakaoAddressSearchClient.findOneByNameAndZoneNo(request.getAddressName(), request.getAddressZoneNo()).getRoad_address();
        SearchAddressDto addressDto = SearchAddressDto.fromRoadAddress(roadAddressDto);
        final NaverDetailBookDto bookDto = naverBookSearchClient.searchBookByIsbn(request.getBookIsbn());
        final UserBook userBook = bookService.registerUserBook(request, bookDto.getChannel().getItem(), customUserDetail.getPK(), addressDto);
        bookSearchService.saveUserBook(request, userBook.getUserId(), bookDto.getChannel().getItem(), customUserDetail.getPK(), addressDto);
        return ResponseEntity.ok(UserBookDto.fromEntity(userBook));
    }

    @PutMapping("/book/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserBookDto> updateUserBook(
            @RequestBody UserBookRequest request,
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable(name = "id") Long userBookId
    ) {
        SearchAddressDto addressDto = null;
        if(request.getAddressName() != null && !request.getAddressName().isBlank()) {
            final KakaoAddressSearchDto.RoadAddressDto roadAddressDto =
                    this.kakaoAddressSearchClient.findOneByNameAndZoneNo(request.getAddressName(), request.getAddressZoneNo()).getRoad_address();
            addressDto = SearchAddressDto.fromRoadAddress(roadAddressDto);
        }
        NaverBookItem naverBookItem = null;
        if(request.getBookIsbn() != null && request.getBookTitle() != null && !request.getBookTitle().isBlank()) {
            naverBookItem = naverBookSearchClient.searchBookByIsbn(request.getBookIsbn()).getChannel().getItem();
        }
        final UserBook userBook = bookService.updateUserBook(request, customUserDetail.getPK(), userBookId, addressDto, naverBookItem);
        bookSearchService.updateUserBook(userBookId, request, addressDto, NaverBookItem.fromBook(userBook.getBook()));
        return ResponseEntity.ok(UserBookDto.fromEntity(userBook));
    }

    @GetMapping("/book/search/naver")
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
    public ResponseEntity<NaverBookItem> getStoredNaverBook(
            @PathVariable(name = "id") Long bookId
    ) {
        return ResponseEntity.ok(
                NaverBookItem.fromBook(this.bookService.findBookById(bookId))
        );
    }

    @GetMapping("/book/test/dummy")
    @PreAuthorize("hasRole('ROLE_ROOT')")
    public ResponseEntity<List<Book>> testDummyBookInsert() {
        return ResponseEntity.ok(testSaveNaverBook());
    }
    @GetMapping("/user_book/test/dummy")
    @PreAuthorize("hasRole('ROLE_ROOT')")
    public ResponseEntity<String> testDummyUserBookInsert() {
        testSaveUserBook();
        return ResponseEntity.ok("ok");
    }

    public List<Book> testSaveNaverBook() {
        NaverBookSearchDto naverBookSearchDto = naverBookSearchClient.getBooksFromName(1, 100, "교학사");
        Set<NaverBookItem> testIsbn = new TreeSet<>(Comparator.comparing(NaverBookItem::getIsbn));
        testIsbn.addAll(naverBookSearchDto.getItems().stream().filter(item -> item.getIsbn() != null).toList());
        naverBookSearchDto = naverBookSearchClient.getBooksFromName(1, 100, "문학동네");
        testIsbn.addAll(naverBookSearchDto.getItems().stream().filter(item -> item.getIsbn() != null).toList());
        naverBookSearchDto = naverBookSearchClient.getBooksFromName(1, 100, "민음사");
        testIsbn.addAll(naverBookSearchDto.getItems().stream().filter(item -> item.getIsbn() != null).toList());
        return bookService.saveAllBooks(testIsbn.stream().toList());
    }


    public SearchAddressDto addSignupData(String zoneNo, String addressName) {
        return SearchAddressDto.builder()
                .zoneNo(zoneNo)
                .addressName(addressName)
                .build();
    }


    @Transactional
    public void testSaveUserBook() {
        List<Book> books = bookService.findBookAll();
        List<User> users = userService.findUserAll();
        Set<String> savedUserBook = new HashSet<>();
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(
                User::getId,
                user -> user
        ));

        Map<Long, Book> bookMap = books.stream().collect(Collectors.toMap(
                Book::getId,
                book -> book
        ));

        for (int i = 0; i < 300; i++) {
            log.info("test");
            User user =  userMap.get(new Random().nextLong(14, 3000));
            Book book = bookMap.get(new Random().nextLong(582, 873));
            if(book == null || user == null || savedUserBook.contains(user.getId() + ":" + book.getId())) {
                continue;
            }
            log.info("test2");

            if(user.getUserAddress() == null || user.getUserAddress().isEmpty()) {
                throw new RuntimeException();
            }

            UserAddress userAddress =user.getUserAddress().get(0);
            SearchAddressDto addressDto = SearchAddressDto.builder().addressName(userAddress.getAddressName())
                    .longitude(userAddress.getLongitude())
                    .zoneNo(userAddress.getZoneNo())
                    .latitude(userAddress.getLatitude())
                    .build();

            BookSellType bookSellType;
            if (i < 200) {
                bookSellType =BookSellType.BOTH;
            } else if(i > 270) {
                bookSellType = BookSellType.SELL;
            } else {
                bookSellType = BookSellType.RENT;
            }

            NaverBookItem bookDto = NaverBookItem.fromBook(book);
            UserBookRequest userBookDto = UserBookRequest.builder()
                    .bookSellType(bookSellType)
                    .userId(user.getId())
                    .bookIsbn(bookDto.getIsbn())
                    .bookTitle(bookDto.getTitle())
                    .rentPrice(BigDecimal.valueOf(new Random().nextLong(1000, 3000)))
                    .sellPrice(BigDecimal.valueOf(new Random().nextLong(5000,30000)))
                    .userBookImageDtos(
                            new ArrayList<>() {{
                                add(new UserBookImageDto(
                                        0L, bookDto.getImage(), 0, true
                                ));
                            }}
                    )
                    .title(book.getTitle())
                    .detail(CommonFunction.subStringCharLength(book.getDescription(), 1000))
                    .build();
            log.info("test3");
            final UserBook userBook = bookService.registerUserBook(userBookDto, bookDto, user.getId(),addressDto);
            bookSearchService.saveUserBook(userBookDto, userBook.getUserId(), bookDto,  user.getId(), addressDto);
            savedUserBook.add(user.getId() + ":" + book.getId());
        }
    }
}
