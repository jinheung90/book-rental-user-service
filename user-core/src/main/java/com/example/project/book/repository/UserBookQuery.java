package com.example.project.book.repository;

import com.example.project.book.dto.QUserBookDto;
import com.example.project.book.dto.UserBookDto;
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


    @Cacheable(cacheNames = "book", sync = true)
    public List<UserBookDto> findAllUserBook(PageRequest pageRequest, String name) {
        return jpaQueryFactory.select(new QUserBookDto(
                userBook.id,
                userBook.name,
                userBook.detail,
                userBook.images
                )).from(userBook)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize()).fetch();
    }

    public Long countUserBook() {
        return jpaQueryFactory.select(userBook.count())
                .from(userBook)
                .fetchFirst();
    }
}
