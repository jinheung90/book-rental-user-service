package com.example.project.book.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "books")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Long isbn;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String link;

    @OneToMany(mappedBy = "book")
    private List<UserBook> userBooks;
}
