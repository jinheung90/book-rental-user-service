package com.example.project.book.store.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_book_addresses")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBookAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address_name")
    private String addressName;

    @Column(name = "zone_no")
    private String zoneNo;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

}