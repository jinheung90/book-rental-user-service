package com.example.project.book.repository;


import com.example.project.book.entity.UserBookLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBookLikeRepository extends JpaRepository<UserBookLike, Long> {
    List<UserBookLike> findByUserBookIdInAndUserIdAndActivityIsTrue(List<Long> ids, Long userId);

    Optional<UserBookLike> findByUserIdAndUserBookId(Long userId, Long userBookId);
}
