package com.example.project.book.store.entity;


import com.example.project.book.dto.UserBookDto;
import com.example.project.book.dto.UserBookImageDto;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_book_address_id")
    private UserBookAddress userBookAddress;

    public void inactive() {
        activity = false;
    }

    public void setBookSellType(BookSellType bookSellType, BigDecimal rentPrice, BigDecimal sellPrice) {
        if(bookSellType == null) {
            return;
        }

        if((bookSellType.equals(BookSellType.BOTH) || bookSellType.equals(BookSellType.RENT)) &&
                this.rentPrice.intValue() <= 0 && (rentPrice == null || rentPrice.intValue() <= 0)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "빌려줄 때 가격이 책정이 안되어 있음 mysql");
        }


        if((bookSellType.equals(BookSellType.BOTH) || bookSellType.equals(BookSellType.SELL)) &&
                this.sellPrice.intValue() <= 0 && (sellPrice == null || sellPrice.intValue() <= 0)) {
            throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "판매 할 때 가격이 책정이 안되어 있음 mysql");
        }


        this.setRentPrice(rentPrice);
        this.setSellPrice(sellPrice);

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

    public void setBook(Book book) {
        this.book = book;
    }

    public void setImages(List<UserBookImage> userBookImages) {
        this.images = userBookImages.stream().sorted(Comparator.comparing(UserBookImage::getImageOrder)).toList();
    }

    public void setUserBookAddress(UserBookAddress userBookAddress) {
        this.userBookAddress = userBookAddress;
    }
}
