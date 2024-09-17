package com.example.project.book.dto;

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
                .userBookImage(userBook.getUserBookImage())
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

    public static SearchBookDto whenSearch(UserBook userBook, UserProfileDto userProfile) {
        final UserBookAddress userBookAddress = userBook.getUserBookAddress();
        final com.example.project.book.store.entity.Book book = userBook.getBook();
        return SearchBookDto.builder()
                .userBookId(userBook.getId())
                .userId(userBook.getUserId())
                .userBookImage(
                        userBook.getImages().stream().map(
                                UserBookImageDto::fromEntity
                        ).toList()
                ).userBookDetail(userBook.getDetail())
                .userBookTitle(userBook.getTitle())
                .addressName(userBookAddress.getAddressName())
                .addressLatitude(userBookAddress.getLatitude())
                .addressLongitude(userBookAddress.getLongitude())
                .addressZoneNo(userBookAddress.getZoneNo())
                .userBookRentPrice(userBook.getRentPrice())
                .userBookSellPrice(userBook.getSellPrice())
                .userBookBookSellType(userBook.getBookSellType())
                .userBookRentState(userBook.getRentState())
                .userBookLikeState(false)
                .naverBookImage(book.getImageUrl())
                .naverBookAuthor(book.getAuthor())
                .naverBookDescription(book.getDescription())
                .naverBookIsbn(book.getIsbn())
                .naverBookTitle(book.getTitle())
                .profileImageUrl(userProfile.getProfileImageUrl())
                .profileNickName(userProfile.getNickName())
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
            this.userBookLikeState = false;
            return;
        }
        this.userBookLikeState = true;
    }

    public void setUserBookImage(List<UserBookImage> userBookImages) {
        if(userBookImages == null) {
            this.userBookImage = new ArrayList<>();
            return;
        }
        this.userBookImage = userBookImages.stream().map(UserBookImageDto::fromEntity).toList();
    }
}
