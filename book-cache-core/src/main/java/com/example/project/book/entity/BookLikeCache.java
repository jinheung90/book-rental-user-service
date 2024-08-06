package com.example.project.book.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookLikeCache {
    private Long userId;
    private Long userBookId;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean state;

    public void changeState() {
        this.state = !this.state;
        this.updatedAt = Instant.now();
    }
}
