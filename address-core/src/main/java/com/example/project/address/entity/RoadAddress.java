package com.example.project.address.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "road_address")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class RoadAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address_name")
    private String addressName;

    @Column(name = "region_1depth_name")
    private String region1depthName;

    @Column(name = "region_2depth_name")
    private String region2depthName;

    @Column(name = "region_3depth_name")
    private String region3depthName;

    @Column(name = "road_name")
    private String roadName;

    @Column(name = "underground_yn")
    private String undergroundYn;

    @Column(name = "main_building_no")
    private String mainBuildingNo;

    @Column(name = "sub_building_no")
    private String subBuildingNo;

    @Column(name = "building_name")
    private String buildingName;

    @Column(name = "zone_no")
    private String zoneNo;

    @Column
    private double y;

    @Column
    private double x;
}
