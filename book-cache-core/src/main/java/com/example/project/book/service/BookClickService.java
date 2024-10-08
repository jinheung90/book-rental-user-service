package com.example.project.book.service;



import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookClickService {
    private static final String USER_BOOK_CLICK_COUNT_KEY = "user-book-click-key:";
    private final RedisTemplate<String, Long> longRedisTemplate;

    public void addClickCount(Long userBookId) {
        ValueOperations<String, Long> operations = longRedisTemplate.opsForValue();
        operations.increment(USER_BOOK_CLICK_COUNT_KEY + userBookId, 1);
    }
    
    public Map<Long, Long> getClickCountsByUserBookIdIn(List<Long> userBookIds) {
        if(userBookIds == null || userBookIds.isEmpty()) {
            return new HashMap<>();
        }
        ValueOperations<String, Long> operations = longRedisTemplate.opsForValue();
        List<Long> values = operations.multiGet(userBookIds.stream().map(id -> USER_BOOK_CLICK_COUNT_KEY + id).toList());
        if(values == null || values.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long ,Long> clickCountMap = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            clickCountMap.put(userBookIds.get(i), values.get(i));
        }
        return clickCountMap;
    }
}
