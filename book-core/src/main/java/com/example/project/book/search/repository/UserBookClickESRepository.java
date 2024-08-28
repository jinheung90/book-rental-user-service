package com.example.project.book.search.repository;


import com.example.project.book.search.doc.UserBook;
import com.example.project.book.search.doc.UserBookClickLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.Instant;
import java.util.List;

public interface UserBookClickESRepository extends ElasticsearchRepository<UserBookClickLog, String> {
    List<UserBookClickLog> findAllByCreatedAtBetween(Instant from, Instant to);
}
