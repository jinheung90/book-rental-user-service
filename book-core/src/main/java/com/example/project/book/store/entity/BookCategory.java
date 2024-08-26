package com.example.project.book.store.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "book_categories")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Boolean activity;

    @OneToMany(mappedBy = "bookCategory")
    private List<UserBookCategory> userBookCategories;

    public void inactive() {
        this.activity = false;
    }
}
