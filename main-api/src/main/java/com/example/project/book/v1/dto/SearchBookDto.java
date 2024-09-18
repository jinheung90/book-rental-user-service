package com.example.project.book.v1.dto;

import com.example.project.book.dto.SearchAddressDto;
import com.example.project.book.dto.UserBookDto;
import com.example.project.book.dto.UserBookImageDto;
import com.example.project.book.dto.UserBookRequest;
import com.example.project.book.search.doc.Book;
import com.example.project.book.store.entity.UserBook;
import com.example.project.book.store.entity.UserBookAddress;
import com.example.project.book.store.entity.UserBookImage;
import com.example.project.book.store.entity.UserBookLike;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;

import com.example.project.user.dto.UserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBookDto {
    private Long userBookId;
    private String userBookTitle;
    private String userBookDetail;
    private List<UserBookImageDto> userBookImage;
    private BookRentalStateType userBookRentState;
    private BigDecimal userBookRentPrice;
    private BigDecimal userBookSellPrice;
    private BookSellType userBookBookSellType;
    private Long userId;
    private boolean userBookLikeState;
    private String naverBookTitle;
//    private String naverBookLink;
    private String naverBookImage;
    private String naverBookAuthor;
    private String naverBookPublisher;
    private String naverBookDescription;
//    private Long naverBookPubdate;
    private Long naverBookIsbn;
    private String addressName;
    private String addressZoneNo;
    private Double addressLongitude;
    private Double addressLatitude;
    protected String profileImageUrl;
    protected String profileNickName;


    public static SearchBookDto toDto(UserBookDto userBook, UserProfileDto userProfile) {
        final SearchAddressDto userBookAddress = userBook.getAddress();
        return SearchBookDto.builder()
                .userBookId(userBook.getId())
                .userId(userBook.getUserId())
                .userBookDetail(userBook.getDetail())
                .userBookTitle(userBook.getTitle())
                .userBookImage(userBook.getImages())
                .addressName(userBookAddress.getAddressName())
                .addressLatitude(userBookAddress.getLatitude())
                .addressLongitude(userBookAddress.getLongitude())
                .addressZoneNo(userBookAddress.getZoneNo())
                .userBookRentPrice(userBook.getRentPrice())
                .userBookSellPrice(userBook.getSellPrice())
                .userBookBookSellType(userBook.getBookSellType())
                .userBookRentState(userBook.getRentState())
                .userBookLikeState(false)
                .naverBookImage(userBook.getBookInfo().getImage())
                .naverBookAuthor(userBook.getBookInfo().getAuthor())
                .naverBookDescription(userBook.getBookInfo().getDescription())
                .naverBookIsbn(userBook.getBookInfo().getIsbn())
                .naverBookTitle(userBook.getBookInfo().getTitle())
                .naverBookPublisher(userBook.getBookInfo().getPublisher())
                .profileImageUrl(userProfile.getProfileImageUrl())
                .profileNickName(userProfile.getNickName())
                .build();
    }
}
