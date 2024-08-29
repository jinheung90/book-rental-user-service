package com.example.project.book.search.doc;

import com.example.project.book.client.dto.NaverBookItem;
import com.example.project.book.client.dto.NaverDetailBookDto;
import com.example.project.common.util.JamoSeparate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    private String title;
    private String titleWordUnits;
    private String link;
    private String image;
    private String author;
    private String publisher;
    private String description;
    private Long discount;
    private Long pubdate;
    private Long isbn;

    public static Book fromBook(NaverBookItem item) {
        return Book.builder()
            .author(item.getAuthor())
            .description(item.getDescription())
            .isbn(item.getIsbn())
            .discount(item.getDiscount())
            .pubdate(item.getPubdate())
            .image(item.getImage())
            .title(item.getTitle())
            .titleWordUnits(JamoSeparate.separate(item.getTitle()))
            .publisher(item.getPublisher())
            .link(item.getLink())
            .build();
    }
}
