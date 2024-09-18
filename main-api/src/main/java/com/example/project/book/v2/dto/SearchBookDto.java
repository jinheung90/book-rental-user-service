package com.example.project.book.v2.dto;

import com.example.project.book.dto.SearchAddressDto;
import com.example.project.book.dto.UserBookDto;
import com.example.project.book.dto.UserBookImageDto;
import com.example.project.book.dto.UserBookRequest;
import com.example.project.book.search.doc.Book;
import com.example.project.book.store.entity.UserBook;
import com.example.project.book.store.entity.UserBookAddress;
import com.example.project.book.store.entity.UserBookImage;
import com.example.project.book.store.entity.UserBookLike;
import com.example.project.common.enums.BookRentalStateType;
import com.example.project.common.enums.BookSellType;
import com.example.project.user.dto.UserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBookDto {
    private UserBookDto userBook;
    private UserProfileDto userProfile;
}
