package com.example.project.book.entity;

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

    @Column
    private String title;

    @Column(unique = true)
    private Long isbn;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column
    private String author;

    @Column
    private Long pubdate;

    @Column
    private Long discount;

    @Column
    private String publisher;

    @Column
    private String description;

    @Column(length = 500)
    private String link;

    @OneToMany(mappedBy = "book")
    private List<UserBook> userBooks;
}
