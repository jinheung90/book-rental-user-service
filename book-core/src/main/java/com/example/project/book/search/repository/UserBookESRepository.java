package com.example.project.book.search.repository;


import com.example.project.book.search.doc.UserBook;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserBookESRepository extends ElasticsearchRepository<UserBook, String> {
    Optional<UserBook> findByBookId(Long bookId);
    List<UserBook> findByBookIdIn(Set<Long> bookId);
}
