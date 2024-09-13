package com.example.project.address.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class AddressDto {

    protected Long id;
    protected String addressName;
    protected String zoneNo;
    protected Double longitude;
    protected Double latitude;

}
