package com.example.project.book.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
