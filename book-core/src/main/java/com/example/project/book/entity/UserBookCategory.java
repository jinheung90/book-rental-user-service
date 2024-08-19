package com.example.project.book.entity;


import jakarta.persistence.*;
import lombok.*;

@Table(name = "user_book_categories")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class UserBookCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_book_id")
    private UserBook userBook;

    @ManyToOne
    @JoinColumn(name = "book_category_id")
    private BookCategory bookCategory;
}
