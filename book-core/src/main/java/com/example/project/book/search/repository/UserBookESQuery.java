package com.example.project.book.search.repository;


import com.example.project.book.dto.UserBookDto;
import com.example.project.book.search.doc.UserBook;

import com.example.project.common.enums.BookSellType;
import com.example.project.common.enums.BookSortType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.AbstractElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserBookESQuery {

    private final IndexCoordinates userBookCoordinates = IndexCoordinates.of("user_book");

    private final String RENT_PRICE_RUNTIME_FILED = "rentPriceForCompare";

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<UserBookDto> searchUserBookQuery(
            String keyword,
            BookSortType sortType,
            Long userId,
            BookSellType bookSellType,
            double longitude,
            double latitude,
            PageRequest pageRequest
    ) {

        List<RuntimeField> runtimeFieldList = new ArrayList<>();
        NativeQueryBuilder qb = NativeQuery.builder();
        if(bookSellType == null || BookSellType.BOTH.equals(bookSellType) || BookSellType.RENT.equals(bookSellType)) {
            RuntimeField rentPrice = new RuntimeField(RENT_PRICE_RUNTIME_FILED, "Integer", "emit(doc['rentPrice'].value * 7");
            runtimeFieldList.add(rentPrice);
        }

        qb.withRuntimeFields(runtimeFieldList);
        this.orderBySortCase(bookSellType, sortType, longitude, latitude, qb);
        long count = elasticsearchOperations.count(qb.build(), userBookCoordinates);
        this.matchKeyword(qb, keyword);

        qb.withPageable(pageRequest);
        SearchHits<UserBook> userBookSearchHits = elasticsearchOperations.search(qb.build(), UserBook.class, userBookCoordinates);

        List<UserBookDto> userBookDtos = userBookSearchHits.stream().map(
                userBookSearchHit -> UserBookDto.fromDoc(userBookSearchHit.getContent())
        ).toList();
        return new PageImpl<>(userBookDtos, pageRequest, count);
    }

    public NativeQueryBuilder matchKeyword(NativeQueryBuilder qb, String keyword) {
        if(keyword == null || keyword.isBlank()) {
            return qb;
        }
        List<IndexBoost> boostList = qb.getIndicesBoost();
        if(boostList == null || boostList.isEmpty()) {
            boostList = new ArrayList<>();
        }
        boostList.add(new IndexBoost("title", 5.0f));
        qb.withIndicesBoost(boostList);
        return qb;
    }

    public NativeQueryBuilder orderBySortCase(BookSellType bookSellType, BookSortType sortType, double longitude, double latitude, NativeQueryBuilder qb) {
        return switch (sortType) {
            case DISTANCE -> qb.withSort(this.geoDistance(longitude, latitude));
            case UPDATED_AT -> qb.withSort(updateAtCase());
            case RECOMMEND -> recommendCase(qb);
            case LOW_PRICE -> qb.withSort(this.priceSortCase(bookSellType));
        };
    }

    private Sort updateAtCase() {
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }

    private NativeQueryBuilder recommendCase(NativeQueryBuilder qb) {
        List<IndexBoost> boostList = qb.getIndicesBoost();
        if(boostList == null || boostList.isEmpty()) {
            boostList = new ArrayList<>();
        }
        boostList.add(new IndexBoost("clickCount", 2.f));
        boostList.add(new IndexBoost("likeCount", 3.f));
        return qb.withIndicesBoost(boostList);
    }

    private Sort geoDistance(double longitude, double latitude) {
        return Sort.by(new GeoDistanceOrder("location", new GeoPoint(latitude, longitude))).ascending();
    }

    private Sort priceSortCase(BookSellType bookSellType) {
        if(bookSellType == null || BookSellType.BOTH.equals(bookSellType)) {
            return Sort.by(Sort.Direction.ASC, "rentPrice", "sellPrice");
        } else if (BookSellType.SELL.equals(bookSellType)) {
            return Sort.by(Sort.Direction.ASC, "sellPrice");
        } else {
            return Sort.by(Sort.Direction.ASC, "rentPrice");
        }
    }
}
