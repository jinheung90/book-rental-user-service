package com.example.project.user.dto;

import com.example.project.user.entity.UserAddress;
import com.example.project.user.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressDto {

    private String addressName;
    private String zoneNo;
    private Double longitude;
    private Double latitude;
    private AddressType addressType;

    public static UserAddressDto fromEntity(UserAddress address) {
        return UserAddressDto.builder()
                .addressName(address.getAddressName())
                .zoneNo(address.getZoneNo())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .addressType(address.getAddressType())
                .build();
    }
    public static List<UserAddressDto> fromEntityList(List<UserAddress> addresses) {
        return addresses.stream().map(address -> UserAddressDto.builder()
                .addressName(address.getAddressName())
                .zoneNo(address.getZoneNo())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .addressType(address.getAddressType())
                .build())
                .toList();

    }
}
