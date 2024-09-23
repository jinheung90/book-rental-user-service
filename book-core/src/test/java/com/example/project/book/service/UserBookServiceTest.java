package com.example.project.book.service;


import com.example.project.book.client.dto.NaverBookItem;
import com.example.project.book.dto.SearchAddressDto;
import com.example.project.book.dto.UserBookDto;
import com.example.project.book.dto.UserBookImageDto;
import com.example.project.book.store.entity.UserBook;
import com.example.project.book.store.repository.BookRepository;
import com.example.project.book.store.repository.UserBookImageRepository;
import com.example.project.book.store.repository.UserBookRepository;
import com.example.project.book.store.service.BookService;
import com.example.project.common.aws.s3.S3Uploader;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("local")
public class UserBookServiceTest {

    private UserBookDto userBookDto;
    private UserBookDto userBookDtoEmpty;
    private UserBook userBook;

    private UserBook result;

    @Mock
    private S3Uploader s3Uploader;
    @Mock
    private UserBookRepository userBookRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserBookImageRepository userBookImageRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void init() {


        userBook = UserBook.builder()
                .userId(1L)
                .bookSellType(BookSellType.SELL)
                .rentPrice(BigDecimal.valueOf(10000))
                .rentState(BookRentalStateType.AVAILABLE)
                .sellPrice(BigDecimal.valueOf(111111))
                .detail("detailbefore")
                .title("titlebefore")
                .id(5L)
                .build();

        result = UserBook.builder()
                .userId(1L)
                .bookSellType(BookSellType.BOTH)
                .rentPrice(BigDecimal.valueOf(50000))
                .rentState(BookRentalStateType.AVAILABLE)
                .sellPrice(BigDecimal.valueOf(100000))
                .detail("detail")
                .title("title")
                .id(5L)
                .build();

    }
}
