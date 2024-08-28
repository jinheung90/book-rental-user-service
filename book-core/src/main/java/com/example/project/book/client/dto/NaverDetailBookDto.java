package com.example.project.book.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NaverDetailBookDto {

    private String version;
    private Channel channel;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Channel {
        private String title;
        private String link;
        private String description;
        private String lastBuildDate;
        private int total;
        private int start;
        private int display;
        private ChannelItem item;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChannelItem {
        private String title;
        private String link;
        private String image;
        private String author;
        private String publisher;
        private String description;
        private Long discount;
        private Long pubdate;
        private Long isbn;
    }
}
