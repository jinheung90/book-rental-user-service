package com.example.project.book.store.repository;


import com.example.project.book.store.entity.UserBookLike;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBookLikeRepository extends JpaRepository<UserBookLike, Long> {

    @EntityGraph(attributePaths = {"userBook"})
    List<UserBookLike> findByUserBookIdInAndUserIdAndActivityIsTrue(List<Long> ids, Long userId);

    Optional<UserBookLike> findByUserIdAndUserBookId(Long userId, Long userBookId);
}
