package com.example.project.book.search.doc;

import com.example.project.book.dto.SearchAddressDto;
import com.example.project.book.dto.UserBookImageDto;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Document(indexName = "user_book")
@Builder
@Getter
@AllArgsConstructor
@Slf4j
@Setting(settingPath = "setting.json")
@Mapping(mappingPath = "mapping.json")
public class UserBook {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String detail;

    @Field(type = FieldType.Text)
    private BookRentalStateType rentState;

    @Field(type = FieldType.Text)
    private String bookTitleWordUnit;

    @Field(type = FieldType.Integer)
    private Integer rentPrice;

    @Field(type = FieldType.Integer)
    private Integer sellPrice;

    @Field(type = FieldType.Text)
    private BookSellType bookSellType;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Object)
    private List<UserBookImageDto> images;

    @Field(type = FieldType.Boolean)
    private boolean activity;

    @Builder.Default
    private Long likeCount = 0L;

    @Field(type = FieldType.Long)
    private Long bookId;

    @Builder.Default
    private Long clickCount = 0L;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant updatedAt;

    @Field(type = FieldType.Object)
    private Book book;

    @Field(type = FieldType.Long)
    private Long addressId;

    @Field(type = FieldType.Text)
    private String addressZoneNo;

    @Field(type = FieldType.Text)
    private String addressName;

    @GeoPointField
    private GeoPoint location;

    public void decreaseLikeCount() {
        if(likeCount <= 0L) {
            log.warn(String.format("like count already 0 id: %s, count = %s", id, likeCount));
        }
        likeCount--;
    }

    public void increaseLikeCount() {
        likeCount++;
    }

    public void setBookSellType(BookSellType bookSellType) {
        if(bookSellType == null) {
            return;
        }

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

    public void setBook(Book book) {
        if(book == null) {
            return;
        }
        this.book = book;
    }



    public void setSellPrice(Integer sellPrice) {
        if(sellPrice == null || sellPrice == 0) {
            return;
        }
        this.sellPrice = sellPrice;
    }

    public void setRentState(BookRentalStateType rentState) {
        if(Objects.isNull(rentState)) return;
        this.rentState = rentState;
    }

    public void setRentPrice(Integer rentPrice) {
        if(rentPrice == null || rentPrice == 0) {
            return;
        }
        this.rentPrice = rentPrice;
    }

    public void setImages(List<UserBookImageDto> userBookImages) {
        this.images = userBookImages;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public void setAddress(SearchAddressDto addressDto) {
        if(addressDto == null || addressDto.getAddressName() == null || addressDto.getAddressName().isBlank()) {
            return;
        }
        this.addressId = addressDto.getId();
        this.addressName = addressDto.getAddressName();
        this.addressZoneNo = addressDto.getZoneNo();
        this.location = new GeoPoint(addressDto.getLatitude(), addressDto.getLongitude());
    }

    public void updateUserBook(
            String title,
            BookSellType bookSellType,
            String detail,
            Book book,
            Integer sellPrice,
            BookRentalStateType rentState,
            Integer rentPrice,
            List<UserBookImageDto> userBookImages,
            SearchAddressDto addressDto)
    {
        this.setTitle(title);
        this.setBookSellType(bookSellType);
        this.setDetail(detail);
        this.setBook(book);
        this.setSellPrice(sellPrice);
        this.setRentState(rentState);
        this.setRentPrice(rentPrice);
        this.setImages(userBookImages);
        this.setAddress(addressDto);
        this.id = "";
    }

    public void inactive() {
        this.activity = false;
        this.id = "";
    }
}
