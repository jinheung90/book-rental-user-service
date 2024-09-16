package com.example.project.book.store.repository;

import com.example.project.book.store.entity.UserBookImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBookImageRepository extends JpaRepository<UserBookImage, Long> {
    List<UserBookImage> findAllByUserBookIdInOrderByImageOrder(List<Long> ids);
}
