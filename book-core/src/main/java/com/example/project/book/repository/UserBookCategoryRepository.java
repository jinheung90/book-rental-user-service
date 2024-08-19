package com.example.project.book.repository;

import com.example.project.book.entity.UserBookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBookCategoryRepository extends JpaRepository<UserBookCategory, Long> {
}
