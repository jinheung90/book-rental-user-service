package com.example.project.book.dto;

import com.example.project.address.dto.KakaoAddressSearchDto;
import com.example.project.book.search.doc.Book;
import com.example.project.book.store.entity.UserBookAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchAddressDto {
    private Long id;
    private String addressName;
    private String zoneNo;
    private Double longitude;
    private Double latitude;

    public static SearchAddressDto fromEntity(UserBookAddress address) {
        return SearchAddressDto.builder()
                .id(address.getId())
                .addressName(address.getAddressName())
                .zoneNo(address.getZoneNo())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    public static UserBookAddress toEntity(SearchAddressDto searchAddressDto) {
        return UserBookAddress.builder()
                .id(searchAddressDto.getId())
                .addressName(searchAddressDto.getAddressName())
                .zoneNo(searchAddressDto.getZoneNo())
                .latitude(searchAddressDto.getLatitude())
                .longitude(searchAddressDto.getLongitude())
                .build();
    }

    public static SearchAddressDto fromRoadAddress(KakaoAddressSearchDto.RoadAddressDto roadAddressDto) {
        return SearchAddressDto.builder()
                .addressName(roadAddressDto.getAddress_name())
                .zoneNo(roadAddressDto.getZone_no())
                .latitude(Double.valueOf(roadAddressDto.getY()))
                .latitude(Double.valueOf(roadAddressDto.getX()))
                .build();
    }

}
