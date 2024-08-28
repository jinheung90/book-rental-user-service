package com.example.project.book.search.doc;

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

    public static Book fromBook(NaverDetailBookDto.ChannelItem channelItem) {
        return Book.builder()
            .author(channelItem.getAuthor())
            .description(channelItem.getDescription())
            .isbn(channelItem.getIsbn())
            .discount(channelItem.getDiscount())
            .pubdate(channelItem.getPubdate())
            .image(channelItem.getImage())
            .title(channelItem.getTitle())
            .titleWordUnits(JamoSeparate.separate(channelItem.getTitle()))
            .publisher(channelItem.getPublisher())
            .link(channelItem.getLink())
            .build();
    }
}
