package com.example.project.book.repository;

import com.example.project.book.dto.QUserBookDto;
import com.example.project.book.dto.UserBookDto;
import com.example.project.book.entity.QBookRentalState;
import com.example.project.book.enums.BookRentalStateType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.project.book.entity.QUserBook.userBook;
import static com.example.project.book.entity.QBookRentalState.bookRentalState;
@Repository
@RequiredArgsConstructor
public class BookRentalStateQuery {
    private final JPAQueryFactory jpaQueryFactory;

//    public Page<UserBookDto> findReviewByUserId(PageRequest pageRequest, Long userId) {
//
//        List<UserBookDto> dtoList = jpaQueryFactory.select(new QUserBookDto(
//                        userBook.id,
//                        userBook.name,
//                        userBook.detail
//                ))
//
//                .innerJoin(bookRentalState.userBook, userBook)
//                .where(userBook.user.id.eq(userId))
//                .where(bookRentalState.state.eq(BookRentalStateType.COMPLETE))
//                .offset(pageRequest.getOffset())
//                .limit(pageRequest.getPageSize())
//                .fetch();
//
//        Long count = jpaQueryFactory.select(userBook.count())
//                .from(userBook)
//                .fetchFirst();
//
//        return new PageImpl<>(dtoList, pageRequest, count);
//    }
}
