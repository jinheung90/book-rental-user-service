package com.example.project.book.store.entity;

import com.example.project.book.client.dto.NaverBookSearchDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "books", indexes = { @Index(name = "isbnIndex", columnList = "isbn", unique = true) })
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private Long isbn;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private Long pubdate;

    @Column(nullable = false)
    private Long discount;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(length = 1023, nullable = false)
    private String link;

    @OneToMany(mappedBy = "book")
    private List<UserBook> userBooks;
}
