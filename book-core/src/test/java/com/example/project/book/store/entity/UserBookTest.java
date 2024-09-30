package com.example.project.book.store.entity;

import com.example.project.book.dto.UserBookDto;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

@Tag("unit")
class UserBookTest {
    @BeforeEach
    void init() {

    }

    @Test
    void nullTest() {
        UserBook userBook = UserBook.builder()
                .userId(1L)
                .book(Book.builder().build())
                .bookSellType(BookSellType.BOTH).build();
        Assertions.assertDoesNotThrow(() -> UserBookDto.fromEntity(userBook));
    }
}
