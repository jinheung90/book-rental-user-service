package com.example.project.common.aws.s3;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BucketType {
    USER("book-rental-user"),
    BOOK("book-rental-book"),
    ;
    private final String name;

    public String getName() {
        return name;
    }
}
