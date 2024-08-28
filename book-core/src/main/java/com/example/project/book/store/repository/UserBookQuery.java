package com.example.project.book.store.repository;

import com.example.project.book.store.entity.UserBook;
import com.example.project.book.store.service.BookService;
import com.example.project.common.enums.BookRentalStateType;


import com.example.project.common.enums.BookSellType;
import com.example.project.common.enums.BookSortType;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.example.project.book.store.entity.QUserBook.userBook;
import static com.example.project.book.store.entity.QUserBookLike.userBookLike;
import static com.example.project.book.store.entity.QBook.book;



@Repository
@RequiredArgsConstructor
public class UserBookQuery {

    private final JPAQueryFactory jpaQueryFactory;

    public List<UserBook> searchUserBook(
            PageRequest pageRequest,
            String name,
            Long userId,
            BookSellType bookSellType,
            BookSortType bookSortType
    ) {
        JPAQuery<UserBook> query = jpaQueryFactory.selectFrom(userBook)
                .innerJoin(userBook.book, book)
                .fetchJoin()
                .where(userBook.rentState.eq(BookRentalStateType.AVAILABLE)
                        .or(userBook.rentState.eq(BookRentalStateType.RENTED)))
                .where(userBook.activity.isTrue())
                .orderBy(userBook.updatedAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());

        query = this.searchName(query, name);
        query = this.searchUserId(query, userId);
        query = this.checkSellType(query, bookSellType);
        return query.fetch();
    }

    public List<UserBook> getWishList(PageRequest pageRequest) {
        JPAQuery<UserBook> query = jpaQueryFactory.selectFrom(userBook)
                .innerJoin(userBook, userBookLike.userBook)
                .fetchJoin()
                .where(userBook.rentState.eq(BookRentalStateType.AVAILABLE))
                .where(userBookLike.activity.isTrue())
                .where(userBook.activity.isTrue())
                .orderBy(userBook.updatedAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());
        return query.fetch();
    }

    public <T> JPAQuery<T> checkSellType(JPAQuery<T> query, BookSellType bookSellType) {
        if(bookSellType == null || bookSellType.equals(BookSellType.BOTH)) {
            return query.where(userBook.bookSellType.eq(BookSellType.SELL)
                    .or(userBook.bookSellType.eq(BookSellType.RENT))
                    .or(userBook.bookSellType.eq(BookSellType.BOTH)));
        } else if(bookSellType.equals(BookSellType.SELL)) {
            return query.where(userBook.bookSellType.eq(BookSellType.SELL)
                    .or(userBook.bookSellType.eq(BookSellType.BOTH)));
        } else if(bookSellType.equals(BookSellType.RENT)) {
            return query.where(userBook.bookSellType.eq(BookSellType.RENT)
                    .or(userBook.bookSellType.eq(BookSellType.BOTH)));
        }

        return query;
    }

    public <T> JPAQuery<T> searchName(JPAQuery<T> query, String name) {
        if(Objects.nonNull(name) && !name.isBlank()) return query.where(book.title.contains(name));
        return query;
    }

    public <T> JPAQuery<T>  searchUserId(JPAQuery<T> query, Long userId) {
        if(userId != null && userId != 0) return query.where(userBook.userId.eq(userId));
        return query;
    }

    public Long countSearchUserBook(String name, Long userId, BookSellType bookSellType) {
        JPAQuery<Long> query = jpaQueryFactory.select(userBook.count())
                .from(userBook)
                .where(userBook.rentState.eq(BookRentalStateType.AVAILABLE)
                        .or(userBook.rentState.eq(BookRentalStateType.RENTED)))
                .where(userBook.activity.isTrue());
        query = this.searchName(query, name);
        query = this.searchUserId(query, userId);
        query = this.checkSellType(query, bookSellType);
        return query.fetchFirst();
    }

    public Long countWishList() {
        JPAQuery<Long> query = jpaQueryFactory.select(userBook.count())
            .from(userBook)
            .where(userBook.rentState.eq(BookRentalStateType.AVAILABLE))
            .where(userBookLike.activity.isTrue())
            .where(userBook.activity.isTrue())
            .orderBy(userBook.updatedAt.desc());
        return query.fetchFirst();
    }
}
