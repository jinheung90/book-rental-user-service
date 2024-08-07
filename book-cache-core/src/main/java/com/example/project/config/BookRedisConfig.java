package com.example.project.config;

import com.example.project.book.entity.BookLikeCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class BookRedisConfig {

    @Bean
    public RedisTemplate<String, BookLikeCache> bookLikeCacheRedisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, BookLikeCache> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(BookLikeCache.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(BookLikeCache.class));
        return redisTemplate;
    }
}
