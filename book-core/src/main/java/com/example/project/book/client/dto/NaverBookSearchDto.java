package com.example.project.book.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class NaverBookSearchDto {
    private RSS rss;

    @Getter
    @AllArgsConstructor
    public static class RSS {
        private Channel channel;
    }

    @Getter
    @AllArgsConstructor
    public static class Channel {
        private Instant lastBuildDate;
        private int total;
        private int start;
        private int display;
        private List<Item> item;
    }

    @Getter
    @AllArgsConstructor
    public static class Item {
        private String title;
        private String link;
        private String image;
        private String author;
        private int isbn;
    }
}
