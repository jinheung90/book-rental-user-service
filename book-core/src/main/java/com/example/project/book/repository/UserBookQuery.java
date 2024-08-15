package com.example.project.book.repository;


import com.example.project.book.entity.UserBook;
import com.example.project.common.enums.BookRentalStateType;


import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Objects;

import static com.example.project.book.entity.QUserBook.userBook;
import static com.example.project.book.entity.QBook.book;



@Repository
@RequiredArgsConstructor
public class UserBookQuery {

    private final JPAQueryFactory jpaQueryFactory;

    public List<UserBook> searchUserBook(PageRequest pageRequest, String name, Long userId) {
        JPAQuery<UserBook> query = jpaQueryFactory.selectFrom(userBook)
                .innerJoin(userBook.book, book)
                .fetchJoin()
                .where(userBook.state.eq(BookRentalStateType.AVAILABLE))
                .where(userBook.activity.isTrue())
                .orderBy(userBook.updatedAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());

        query = this.searchName(query, name);
        query = this.searchUserId(query, userId);
        return query.fetch();
    }

    public <T> JPAQuery<T> searchName(JPAQuery<T> query, String name) {
        if(Objects.nonNull(name) && !name.isBlank()) return query.where(book.name.contains(name));
        return query;
    }

    public <T> JPAQuery<T>  searchUserId(JPAQuery<T> query, Long userId) {
        if(userId != null && userId != 0) return query.where(userBook.userId.eq(userId));
        return query;
    }

    public Long countSearchUserBook(String name, Long userId) {
        JPAQuery<Long> query = jpaQueryFactory.select(userBook.count())
                .from(userBook)
                .where(userBook.state.eq(BookRentalStateType.AVAILABLE));
        query = this.searchName(query, name);
        query = this.searchUserId(query, userId);
        return query.fetchFirst();
    }
}
