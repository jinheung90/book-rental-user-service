package com.example.project.book.store.repository;

import com.example.project.book.store.entity.UserBookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBookCategoryRepository extends JpaRepository<UserBookCategory, Long> {
}
