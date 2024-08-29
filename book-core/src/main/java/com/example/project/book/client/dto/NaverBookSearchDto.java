package com.example.project.book.client.dto;

import com.example.project.book.store.entity.Book;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NaverBookSearchDto {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<NaverBookItem> items;
}
