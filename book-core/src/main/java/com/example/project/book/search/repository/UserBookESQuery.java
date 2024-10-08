package com.example.project.book.search.repository;


import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.search.FieldSuggesterBuilders;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import com.example.project.book.dto.UserBookDto;
import com.example.project.book.search.doc.UserBook;

import com.example.project.common.enums.BookSellType;
import com.example.project.common.enums.BookSortType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
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

import static com.example.project.common.enums.BookSortType.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserBookESQuery {

    private final IndexCoordinates userBookCoordinates = IndexCoordinates.of("user_book");

    private final String RENT_PRICE_RUNTIME_FILED = "rentPriceForCompare";

    private final ElasticsearchOperations elasticsearchOperations;


    //TODO 검색 쿼리 작성 현재 GeoDistance 조건절 에러
    public SearchHits<UserBook> searchUserBookQuery(
            String keyword,
            BookSortType sortType,
            Long userId,
            BookSellType bookSellType,
            Double longitude,
            Double latitude,
            PageRequest pageRequest
    ) {

        List<RuntimeField> runtimeFieldList = new ArrayList<>();
        NativeQueryBuilder qb = NativeQuery.builder();
//        if(bookSellType == null || BookSellType.BOTH.equals(bookSellType) || BookSellType.RENT.equals(bookSellType)) {
//            RuntimeField rentPrice = new RuntimeField(RENT_PRICE_RUNTIME_FILED, "Integer", "emit(doc['rentPrice'].value * 7");
//            runtimeFieldList.add(rentPrice);
//        }
//        qb.withRuntimeFields(runtimeFieldList);

//        this.orderBySortCase(bookSellType, sortType, longitude, latitude, qb);
//        this.matchKeyword(qb, keyword);
        qb.withPageable(pageRequest);
        return elasticsearchOperations.search(qb.build(), UserBook.class, userBookCoordinates);
    }

//    public NativeQueryBuilder matchKeyword(NativeQueryBuilder qb, String keyword) {
//        if(keyword == null || keyword.isBlank()) {
//            return qb;
//        }
//        qb.withQuery(QueryBuilders.bool()
//                .should(new ArrayList<>() {{
//                    add(QueryBuilders.term())
//                }})
//        )
//        qb.withIndicesBoost(boostList);
//        return qb;
//    }

    public NativeQueryBuilder orderBySortCase(BookSellType bookSellType, BookSortType sortType, Double longitude, Double latitude, NativeQueryBuilder qb) {
        log.info("c");
        log.info(sortType.toString());
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

    private Sort geoDistance(Double longitude, Double latitude) {
        log.info("d");
        if(latitude == null || longitude == null) {
            return this.updateAtCase();
        }
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
