package com.example.project.book.search.doc;

import com.example.project.book.client.dto.NaverDetailBookDto;
import com.example.project.book.dto.UserBookImageDto;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(indexName = "user_book")
@Builder
@Getter
@AllArgsConstructor
public class UserBook {

    @Id
    private String id;

    private String title;

    private String titleWordUnits;

    private String detail;

    private BookRentalStateType rentState;

    private BigDecimal rentPrice;

    private BigDecimal sellPrice;

    private BookSellType bookSellType;

    private Long userId;

    private List<UserBookImageDto> imageUrls;

    private boolean activity;

    private Instant createdAt;

    private Instant updatedAt;

    private Long bookId;

    @Field(type = FieldType.Object)
    private Book book;

    @Builder.Default
    private Long likeCount = 0L;
}
