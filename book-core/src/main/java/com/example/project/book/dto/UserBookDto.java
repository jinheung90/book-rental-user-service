package com.example.project.book.dto;

import com.example.project.book.client.dto.NaverBookItem;
import com.example.project.book.client.dto.NaverBookSearchDto;
import com.example.project.book.store.entity.UserBook;

import com.example.project.book.store.entity.UserBookAddress;
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
    private List<UserBookImageDto> userBookImageDtos;

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

    public static UserBookDto fromEntity(UserBook userBook) {
        final UserBookAddress userBookAddress = userBook.getUserBookAddress();
        return UserBookDto.builder()
                .id(userBook.getId())
                .userId(userBook.getUserId())
                .userBookImageDtos(
                        userBook.getImages().stream().map(
                                UserBookImageDto::fromEntity
                        ).toList()
                ).detail(userBook.getDetail())
                .title(userBook.getBook().getTitle())
                .address(
                        SearchAddressDto.fromEntity(userBookAddress)
                )
                .bookInfo(NaverBookItem.fromBook(userBook.getBook()))
                .rentPrice(userBook.getRentPrice())
                .sellPrice(userBook.getSellPrice())
                .bookSellType(userBook.getBookSellType())
                .rentState(userBook.getRentState())
                .build();
    }

    public static UserBookDto whenSearch(UserBook userBook, UserBookLike userBookLike) {
        UserBookDto userBookDto = UserBookDto.fromEntity(userBook);
        if(userBookLike != null && userBookLike.isActivity()) {
            userBookDto.setBookLikeState(true);
        }
        return userBookDto;
    }

    public void setBookLikeState(boolean bookLikeState) {
        this.bookLikeState = bookLikeState;
    }
}
