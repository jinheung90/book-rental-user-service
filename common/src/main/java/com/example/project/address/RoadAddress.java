package com.example.project.address;


import lombok.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class RoadAddress {

    protected Long id;
    protected String addressName;
    protected String region1depthName;
    protected String region2depthName;
    protected String region3depthName;
    protected String roadName;
    protected String undergroundYn;
    protected String mainBuildingNo;
    protected String subBuildingNo;
    protected String buildingName;
    protected String zoneNo;
    protected Double longitude;
    protected Double latitude;
}
