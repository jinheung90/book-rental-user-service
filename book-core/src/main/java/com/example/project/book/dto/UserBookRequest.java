package com.example.project.book.dto;

import com.example.project.book.client.dto.NaverBookItem;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserBookRequest {
    private Long id;
    private String title;
    private String detail;
    private List<UserBookImageDto> userBookImageDtos;
    private BookRentalStateType rentState;
    private BigDecimal rentPrice;
    private BigDecimal sellPrice;
    private BookSellType bookSellType;
    private Long userId;
    private String bookTitle;
    private Long bookIsbn;
    private String addressName;
    private String addressZoneNo;
}
