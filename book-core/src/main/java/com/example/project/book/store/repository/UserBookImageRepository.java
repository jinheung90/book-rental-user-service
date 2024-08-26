package com.example.project.book.store.repository;

import com.example.project.book.store.entity.UserBookImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBookImageRepository extends JpaRepository<UserBookImage, Long> {
}
