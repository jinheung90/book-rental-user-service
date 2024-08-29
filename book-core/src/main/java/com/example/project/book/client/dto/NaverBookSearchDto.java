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
    private List<Item> items;


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Item {

        private String title;
        private String link;
        private String image;
        private String author;
        private String publisher;
        private String description;
        private Long discount;
        private Long pubdate;
        private Long isbn;

        public static Item fromBook(Book book) {
            return Item.builder()
                    .author(book.getAuthor())
                    .description(book.getDescription())
                    .image(book.getImageUrl())
                    .isbn(book.getIsbn())
                    .publisher(book.getPublisher())
                    .link(book.getLink())
                    .title(book.getTitle())
                    .pubdate(book.getPubdate())
                    .discount(book.getDiscount())
                    .build();
        }
    }
}
