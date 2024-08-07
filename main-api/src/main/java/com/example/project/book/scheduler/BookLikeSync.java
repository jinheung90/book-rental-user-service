package com.example.project.book.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.datatype.Duration;

@Component
@RequiredArgsConstructor
public class BookLikeSync {
    // 실제로는 배치 또는 스케줄러 서버를 하나 따로 띄워야 하지만 일종의 포트폴리오 이므로 api에 편입 후 shedlock을 사용할 예정
    // 책의 좋아요를 redis -> mysql로 동기화 하는 시스템

    @Scheduled(cron = "0 * 1 * * ?")
    public void bookLikeSync() {

    }
}
