package com.example.project.book.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;


public class SearchPageImpl<T> extends PageImpl<T> {

    private String suggest;

    public SearchPageImpl(List<T> content, Pageable pageable, long total, String suggest) {
        super(content, pageable, total);
        this.suggest = suggest;
    }
}
