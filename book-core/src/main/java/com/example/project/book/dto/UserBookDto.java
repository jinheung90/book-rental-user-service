package com.example.project.book.dto;

import com.example.project.book.client.dto.NaverBookItem;
import com.example.project.book.client.dto.NaverBookSearchDto;
import com.example.project.book.search.doc.Book;
import com.example.project.book.store.entity.UserBook;

import com.example.project.book.store.entity.UserBookAddress;
import com.example.project.book.store.entity.UserBookImage;
import com.example.project.book.store.entity.UserBookLike;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "유저의 책 정보")
public class UserBookDto {

    @Schema(description = "아이디")
    private Long id;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "상세 정보")
    private String detail;

    @Schema(description = "책 이미지")
    private List<UserBookImageDto> userBookImage;

    @Schema(description = "책 대여 상태")
    private BookRentalStateType rentState;

    @Schema(description = "대여 가격")
    private BigDecimal rentPrice;

    @Schema(description = "판매 가격")
    private BigDecimal sellPrice;

    @Schema(description = "책 판매 타입")
    private BookSellType bookSellType;

    @Schema(description = "유저 아이디")
    private Long userId;

    @Schema(description = "address")
    private SearchAddressDto address;

    @Schema(description = "네이버 책 정보")
    private NaverBookItem bookInfo;

    @Schema(description = "좋아요 클릭 상태")
    @Builder.Default
    private boolean bookLikeState = false;

    @Builder.Default
    private Long clickCount = 0L;

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public static UserBookDto fromEntity(UserBook userBook) {
        final UserBookAddress userBookAddress = userBook.getUserBookAddress();
        return UserBookDto.builder()
                .id(userBook.getId())
                .userId(userBook.getUserId())
                .userBookImage(
                        userBook.getImages().stream().map(
                                UserBookImageDto::fromEntity
                        ).toList()
                ).detail(userBook.getDetail())
                .title(userBook.getTitle())
                .address(
                        SearchAddressDto.fromEntity(userBookAddress)
                )
                .bookInfo(NaverBookItem.fromBook(userBook.getBook()))
                .rentPrice(userBook.getRentPrice())
                .sellPrice(userBook.getSellPrice())
                .bookSellType(userBook.getBookSellType())
                .clickCount(0L)
                .rentState(userBook.getRentState())
                .build();
    }

    public static UserBookDto whenSearch(UserBook userBook) {
        return UserBookDto.builder()
                .id(userBook.getId())
                .userId(userBook.getUserId())
                .detail(userBook.getDetail())
                .title(userBook.getTitle())
                .address(
                        SearchAddressDto.fromEntity(userBook.getUserBookAddress())
                )
                .bookInfo(NaverBookItem.fromBook(userBook.getBook()))
                .rentPrice(userBook.getRentPrice())
                .sellPrice(userBook.getSellPrice())
                .bookSellType(userBook.getBookSellType())
                .clickCount(0L)
                .rentState(userBook.getRentState())
                .build();
    }

    public static UserBookDto fromDoc(com.example.project.book.search.doc.UserBook userBook) {

        return UserBookDto.builder()
                .id(userBook.getBookId())
                .userId(userBook.getUserId())
                .userBookImage(userBook.getImages())
                .detail(userBook.getDetail())
                .title(userBook.getBook().getTitle())
                .address(
                        SearchAddressDto.fromDoc(userBook)
                )
                .bookInfo(Book.fromBook(userBook.getBook()))
                .rentPrice(BigDecimal.valueOf(userBook.getRentPrice()))
                .sellPrice(BigDecimal.valueOf(userBook.getSellPrice()))
                .bookSellType(userBook.getBookSellType())
                .clickCount(0L)
                .rentState(userBook.getRentState())
                .build();
    }


    public static UserBook toEntity(UserBookRequest request, com.example.project.book.store.entity.Book book, Long userId, SearchAddressDto addressDto) {
        UserBook userBook = UserBook.builder()
                .rentPrice(request.getRentPrice())
                .bookSellType(request.getBookSellType())
                .sellPrice(request.getSellPrice())
                .rentState(BookRentalStateType.AVAILABLE)
                .bookSellType(request.getBookSellType())
                .userBookAddress(UserBookAddress.builder()
                        .addressName(addressDto.getAddressName())
                        .zoneNo(addressDto.getZoneNo())
                        .longitude(addressDto.getLongitude())
                        .latitude(addressDto.getLatitude())
                        .build())
                .activity(true)
                .book(book)
                .userId(userId)
                .detail(request.getDetail())
                .title(request.getTitle())
                .build();
        userBook.setBookSellType(request.getBookSellType(), request.getRentPrice(), request.getSellPrice());
        return userBook;
    }

    public void setBookLikeState(UserBookLike bookLikeState) {
        if(bookLikeState == null || !bookLikeState.isActivity()) {
            this.bookLikeState = false;
            return;
        }
        this.bookLikeState = true;
    }

    public void setUserBookImage(List<UserBookImage> userBookImages) {
        if(userBookImages == null) {
            this.userBookImage = new ArrayList<>();
            return;
        }
        this.userBookImage = userBookImages.stream().map(UserBookImageDto::fromEntity).toList();
    }
}
