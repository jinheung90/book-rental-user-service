package com.example.project.book.store.entity;


import com.example.project.book.dto.UserBookDto;
import com.example.project.book.dto.UserBookImageDto;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Table(name = "user_books")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class UserBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1023)
    private String detail;

    @Column(name = "book_rent_state", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private BookRentalStateType rentState;

    @Column(name = "rent_price")
    private BigDecimal rentPrice;

    @Column(name = "sell_price")
    private BigDecimal sellPrice;

    @Column(name = "book_sell_type", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private BookSellType bookSellType;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "userBook", cascade = CascadeType.ALL)
    @BatchSize(size = 3)
    @OrderBy("imageOrder asc")
    private List<UserBookImage> images;

    @OneToMany(mappedBy = "userBook")
    private List<UserBookCategory> categories;

    @Column
    private boolean activity = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    public void inactive() {
        activity = false;
    }

    public void setBookSellType(BookSellType bookSellType) {
        this.bookSellType = bookSellType;
    }

    public void setDetail(String detail) {
        if(detail == null || detail.isBlank()) {
            return;
        }
        this.detail = detail;
    }

    public void setTitle(String title) {
        if(title == null || title.isBlank()) {
            return;
        }
        this.title = title;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        if(sellPrice == null || sellPrice.equals(BigDecimal.valueOf(0))) {
            return;
        }
        this.sellPrice = sellPrice;
    }

    public void setRentState(BookRentalStateType rentState) {
        if(Objects.isNull(rentState)) return;
        this.rentState = rentState;
    }

    public void setRentPrice(BigDecimal rentPrice) {
        if(rentPrice == null || rentPrice.equals(BigDecimal.valueOf(0))) {
            return;
        }
        this.rentPrice = rentPrice;
    }

    public void setImages(List<UserBookImage> userBookImages) {
        this.images = userBookImages;
    }

    public static UserBookDto toDto(UserBook userBook) {
        return UserBookDto.builder()
                .id(userBook.getId())
                .userId(userBook.getUserId())
                .userBookImageDtos(
                        userBook.getImages().stream().map(
                                UserBookImageDto::fromEntity
                        ).toList()
                ).detail(userBook.getDetail())
                .title(userBook.getBook().getTitle())
                .bookInfo(userBook.getBook().)
                .rentPrice(userBook.getRentPrice())
                .sellPrice(userBook.getSellPrice())
                .bookSellType(userBook.getBookSellType())
                .rentState(userBook.getRentState())
                .build();
    }
}
