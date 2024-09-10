package com.example.project.user.dto;

import com.example.project.address.dto.AddressDto;
import com.example.project.user.entity.UserAddress;
import com.example.project.user.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserAddressDto extends AddressDto {

    private AddressType addressType;

    public static UserAddressDto fromEntity(UserAddress address) {
        return UserAddressDto.builder()
                .id(address.getId())
                .addressName(address.getAddressName())
                .zoneNo(address.getZoneNo())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .addressType(address.getAddressType())
                .build();
    }
    public static List<UserAddressDto> fromEntityList(List<UserAddress> addresses) {
        if(addresses == null || addresses.isEmpty()) {
            return new ArrayList<>();
        }

        return addresses.stream().map(UserAddressDto::fromEntity).toList();
    }
}
