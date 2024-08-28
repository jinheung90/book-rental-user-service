package com.example.project.book.scheduler;

import com.example.project.book.dto.UserBookClickCountDto;

import com.example.project.book.search.doc.UserBookClickLog;
import com.example.project.book.search.service.BookSearchService;

import com.example.project.book.store.service.BookService;
import com.example.project.common.util.TimeUtilFunction;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
public class BookScheduler {


    private final BookService bookService;
    private final BookSearchService bookSearchService;

    @Scheduled(cron = "0 */2 * * * *")
    @SchedulerLock(name = "clickCountSyncScheduler")
    public void BookClickCountSync() {

        ZonedDateTime now = TimeUtilFunction.getNowAtSeoul();
        Instant searchStart = now.minusDays(30).toInstant();
        Instant searchEnd = now.toInstant();

        List<UserBookClickLog> logs = bookSearchService.getClickLogWithRange(searchStart, searchEnd);
        HashMap<Long, UserBookClickCountDto> userBookClickCountDtoHashMap = new HashMap<>();
        for (UserBookClickLog u: logs
             ) {
            UserBookClickCountDto userBookClickCountDto = userBookClickCountDtoHashMap.get(u.getUserBookId());
            if(userBookClickCountDto == null) {
                userBookClickCountDto = new UserBookClickCountDto(u.getUserBookId(), 0L);
                userBookClickCountDtoHashMap.put(u.getUserBookId(), userBookClickCountDto);
            } else {
                userBookClickCountDto.increaseCount();
            }
        }

        bookSearchService.updateUserBookClickCount(userBookClickCountDtoHashMap);
    }
}
