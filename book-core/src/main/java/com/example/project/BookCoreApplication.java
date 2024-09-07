package com.example.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(exclude = {ElasticsearchDataAutoConfiguration.class})
public class BookCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookCoreApplication.class, args);
    }
}
