package com.example.project.book.service;


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
        userBookDto = UserBookDto.builder()
                .userId(1L)
                .bookLikeState(true)
                .bookSellType(BookSellType.BOTH)
                .rentPrice(BigDecimal.valueOf(50000))
                .rentState(BookRentalStateType.AVAILABLE)
                .sellPrice(BigDecimal.valueOf(100000))
                .detail("detail")
                .title("title")
                .userBookImageDtos(new ArrayList<>() {{
                    add(new UserBookImageDto(1L, "imageurl", 0, true));
                }})
                .build();
        userBookDtoEmpty = UserBookDto.builder()
                .userId(1L)
                .bookLikeState(true)
                .bookSellType(BookSellType.BOTH)
                .rentPrice(BigDecimal.valueOf(50000))
                .rentState(BookRentalStateType.AVAILABLE)
                .sellPrice(BigDecimal.valueOf(100000))
                .detail("")
                .title("")
                .userBookImageDtos(new ArrayList<>() {{
                    add(new UserBookImageDto(1L, "imageurl", 0, false));
                }})
                .build();

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

    @Test
    void updateUserBookNotFindThenThrow() {
        BDDMockito.given(userBookRepository.findByUserIdAndId(1L, 3L)).willThrow(RuntimeExceptionWithCode.class);
        Assertions.assertThrows(RuntimeExceptionWithCode.class, () -> bookService.updateUserBook(userBookDto, 1L, 3L, new SearchAddressDto()));
    }

    @Test
    void updateUserBookTestWhenExistsUpdate() {
        BDDMockito.given(userBookRepository.findByUserIdAndId(1L, 5L)).willReturn(Optional.of(userBook));
        UserBook actual = bookService.updateUserBook(userBookDto, 1L, 5L, new SearchAddressDto());
        Assertions.assertAll(
                () -> Assertions.assertEquals(result.getTitle(), actual.getTitle()),
                () -> Assertions.assertEquals(result.getBookSellType(), actual.getBookSellType()),
                () -> Assertions.assertEquals(result.getDetail(), actual.getDetail())
        );
    }

    @Test
    void updateUserBookTestWhenTitleEmptyString() {
        BDDMockito.given(userBookRepository.findByUserIdAndId(1L, 5L)).willReturn(Optional.of(userBook));
        UserBook actual = bookService.updateUserBook(userBookDtoEmpty, 1L, 5L, new SearchAddressDto());
        Assertions.assertAll(
                () -> Assertions.assertEquals("titlebefore", actual.getTitle()),
                () -> Assertions.assertEquals("detailbefore", actual.getDetail())
        );
    }
}
