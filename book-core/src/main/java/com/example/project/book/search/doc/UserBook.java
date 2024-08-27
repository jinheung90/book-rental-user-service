package com.example.project.book.search.doc;

import com.example.project.book.client.dto.NaverDetailBookDto;
import com.example.project.book.dto.UserBookImageDto;
import com.example.project.book.store.entity.UserBookImage;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import com.example.project.common.util.JamoSeparate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import software.amazon.awssdk.services.ssm.endpoints.internal.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Document(indexName = "user_book")
@Builder
@Getter
@AllArgsConstructor
@Slf4j
public class UserBook {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String titleWordUnits;

    @Field(type = FieldType.Text)
    private String detail;

    @Field(type = FieldType.Text)
    private BookRentalStateType rentState;

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
    private Instant createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant updatedAt;

    @Field(type = FieldType.Object)
    private Book book;


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
        this.titleWordUnits = JamoSeparate.separate(title);
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
}
