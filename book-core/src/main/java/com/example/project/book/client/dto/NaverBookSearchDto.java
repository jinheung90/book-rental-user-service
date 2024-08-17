package com.example.project.book.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
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
    private List<Item> items;


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private String title;
        private String link;
        private String image;
        private String author;
        private String publisher;
        private String description;
        private int discount;
        private Long pubdate;
        private Long isbn;
    }
}
