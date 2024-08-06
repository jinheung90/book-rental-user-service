package com.example.project.book.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.datatype.Duration;

@Component
@RequiredArgsConstructor
public class BookLikeSync {
    // TODO 스케줄러 분리 
    // 분리 되기전 여러서버 대응

    @Scheduled(cron = "0 * 1 * * ?")
    public void bookLikeSync() {

    }
}
