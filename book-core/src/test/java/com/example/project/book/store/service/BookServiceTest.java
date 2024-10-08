package com.example.project.book.store.service;


import com.example.project.book.client.dto.NaverBookItem;
import com.example.project.book.dto.SearchAddressDto;
import com.example.project.book.dto.UserBookDto;
import com.example.project.book.dto.UserBookImageDto;
import com.example.project.book.dto.UserBookRequest;

import com.example.project.book.store.repository.BookRepository;
import com.example.project.book.store.repository.UserBookImageRepository;
import com.example.project.book.store.repository.UserBookRepository;
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
class BookServiceTest {

    private UserBookDto userBookDto;
    private UserBookDto userBookDtoEmpty;

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



    @Test
    void updateUserBookNotExistsTest() {
        BDDMockito.given(userBookRepository.findByUserIdAndId(1L, 2L)).willReturn(Optional.empty());
        Assertions.assertThrows(
                RuntimeExceptionWithCode.class,
                () -> bookService.updateUserBook(UserBookRequest.builder().build(), 1L, 2L, new SearchAddressDto(), NaverBookItem.builder().build())
        );
    }

    @Test
    void checkMainImageCountMultiMainImageTest() {
        Assertions.assertFalse(
                bookService.checkMainImageCount(
                        new ArrayList<>() {{
                            add(UserBookImageDto.builder().mainImage(true)
                                    .imageUrl("a")
                                    .imageOrder(1)
                                    .build()
                            );
                            add(UserBookImageDto.builder().mainImage(true)
                                    .imageUrl("a")
                                    .imageOrder(2)
                                    .build()
                            );
                        }}
                )
        );
    }

}
