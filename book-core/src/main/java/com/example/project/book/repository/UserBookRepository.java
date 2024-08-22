package com.example.project.book.repository;


import com.example.project.book.entity.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    List<UserBook> findAllByUserId(Long userId);
    Optional<UserBook> findByUserIdAndId(Long userId, Long bookId);
}
