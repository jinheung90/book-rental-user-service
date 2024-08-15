package com.example.project.book.service;

import com.example.project.book.entity.BookLikeCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookLikeCacheService {

    private final RedisTemplate<String, BookLikeCache> bookLikeCacheRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private static final String BOOK_LIKE_KEY_PREFIX = "book-like:";
    private static final String BOOK_LIKE_SYNC_KEY = "book-like-sync";

    public BookLikeCache setBookLike(Long userId, Long userBookId) {
        final String key = BOOK_LIKE_KEY_PREFIX + userId.toString();
        BookLikeCache bookLikeCache = (BookLikeCache) bookLikeCacheRedisTemplate.opsForHash().get(key, userBookId);
        if(bookLikeCache == null) {
            bookLikeCache = BookLikeCache.builder()
                    .userBookId(userBookId)
                    .state(false)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .userId(userId)
                    .build();
        } else {
            bookLikeCache.changeState();
        }
        bookLikeCacheRedisTemplate.opsForHash().put(key, userBookId, bookLikeCache);
        this.setBookLikeSync(key);
        return bookLikeCache;
    }

    public void setBookLikeSync(String bookLikeKey) {
        stringRedisTemplate.opsForList().leftPush(BOOK_LIKE_SYNC_KEY, bookLikeKey);
    }

    public List<BookLikeCache> getBookLikeForSync() {
        Long size = stringRedisTemplate.opsForList().size(BOOK_LIKE_SYNC_KEY);
        if(size == null || size == 0) {
            return new ArrayList<>();
        }
        List<String> keys = stringRedisTemplate.opsForList().leftPop(BOOK_LIKE_SYNC_KEY, size);
        return bookLikeCacheRedisTemplate.opsForValue().multiGet(keys);
    }

    public List<BookLikeCache> getBookLikeByUser(Long userId) {
        HashOperations<String, String, BookLikeCache> hashOperations = bookLikeCacheRedisTemplate.opsForHash();
        final String key = BOOK_LIKE_KEY_PREFIX + userId.toString();
        final Set<String> hashKeys = hashOperations.keys(key);
        return hashOperations.multiGet(key, hashKeys).stream().sorted(
                Comparator.comparing(BookLikeCache::getUpdatedAt)
        ).toList();
    }
}
