package com.example.project.book.store.repository;

import com.example.project.book.store.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    List<BookCategory> findAllByActivityIsTrueAndIdIn(List<Long> ids);
}
