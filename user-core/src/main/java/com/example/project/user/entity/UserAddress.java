package com.example.project.user.entity;

import com.example.project.user.enums.AddressType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_address")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress {

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
    private Double longitude;

    @Column
    private Double latitude;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private AddressType addressType;
}
