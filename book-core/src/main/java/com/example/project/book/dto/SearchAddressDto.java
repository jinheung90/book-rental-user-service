package com.example.project.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchAddressDto {
    private Long id;
    private String addressName;
    private String zoneNo;
    private Double x;
    private Double y;
}
