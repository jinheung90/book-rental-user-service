package com.example.project.book.search.repository;


import com.example.project.book.search.doc.UserBook;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
public interface UserBookESRepository extends ElasticsearchRepository<UserBook, String> {

}
