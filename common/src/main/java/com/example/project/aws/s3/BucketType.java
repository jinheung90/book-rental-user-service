package com.example.project.aws.s3;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BucketType {
    USER("user"),
    BOOK("book"),
    ;
    private final String name;

    public String getName() {
        return name;
    }
}
