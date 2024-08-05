package com.example.project.book.repository;

import com.example.project.auth.entity.QUser;
import com.example.project.book.dto.QUserBookDto;
import com.example.project.book.dto.UserBookDto;
import com.example.project.enums.BookRentalStateType;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import java.util.List;

import static com.example.project.book.entity.QUserBook.userBook;
@Repository
@RequiredArgsConstructor
public class UserBookQuery {

    private final JPAQueryFactory jpaQueryFactory;

    public List<UserBookDto> searchUserBook(PageRequest pageRequest, String name, Long userId) {
        JPAQuery<UserBookDto> query = jpaQueryFactory.select(new QUserBookDto(
                userBook.id,
                userBook.name,
                userBook.detail,
                userBook.images,
                userBook.state,
                userBook.user
                )).from(userBook)
                .join(userBook.user, QUser.user)
                .fetchJoin()
                .where(userBook.state.eq(BookRentalStateType.AVAILABLE))
                .orderBy(userBook.updatedAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());
        query = this.searchName(query, name);
        query = this.searchUserId(query, userId);
        return query.fetch();
    }

    public <T> JPAQuery<T> searchName(JPAQuery<T> query, String name) {
        if(!name.isBlank()) return query.where(userBook.name.contains(name));
        return query;
    }
    public <T> JPAQuery<T>  searchUserId(JPAQuery<T> query, Long userId) {
        if(userId != null && userId != 0) return query.where(userBook.user.id.eq(userId));
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
