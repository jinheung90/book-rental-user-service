package com.example.project.book.client.dto;

import com.example.project.book.store.entity.Book;
import com.example.project.common.util.CommonFunction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.annotations.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NaverBookItem {

    private String title;
    private String link;
    private String image;
    private String author;
    private String publisher;
    private String description;
    private Long discount;
    private Long pubdate;
    @NotEmpty
    private Long isbn;

    private static final int descriptionSubNum = 2000;

    public static NaverBookItem fromBook(Book book) {
        return NaverBookItem.builder()
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

    public static Book toEntity(NaverBookItem item) {
        return Book.builder()
                .isbn(item.getIsbn())
                .author(item.getAuthor())
                .link(item.getLink())
                .description(CommonFunction.subStringCharLength(item.getDescription(), descriptionSubNum))
                .discount(item.getDiscount())
                .pubdate(item.getPubdate())
                .imageUrl(item.getImage())
                .publisher(item.getPublisher())
                .title(item.getTitle())
                .build();
    }
}
