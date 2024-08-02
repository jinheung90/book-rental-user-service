package com.example.project.book.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "user_book_images")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserBookImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer order;

    @Column(name = "image_url", length = 1023)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_book_id")
    private UserBook userBook;
}
