package com.example.project.book.search.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.project.book.search.doc.UserBookClickLog;
import lombok.RequiredArgsConstructor;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserBookESQuery {

    private final ElasticsearchOperations elasticsearchOperations;

}
