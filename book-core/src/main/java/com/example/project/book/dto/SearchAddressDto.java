package com.example.project.book.dto;

import com.example.project.address.dto.AddressDto;
import com.example.project.address.dto.KakaoAddressSearchDto;
import com.example.project.book.search.doc.Book;
import com.example.project.book.search.doc.UserBook;
import com.example.project.book.store.entity.UserBookAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@AllArgsConstructor
@SuperBuilder
public class SearchAddressDto extends AddressDto {

    public static SearchAddressDto fromEntity(UserBookAddress address) {
        return SearchAddressDto.builder()
                .id(address.getId())
                .addressName(address.getAddressName())
                .zoneNo(address.getZoneNo())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    public static SearchAddressDto fromDoc(UserBook userBook) {
        return SearchAddressDto.builder()
                .id(userBook.getAddressId())
                .addressName(userBook.getAddressName())
                .zoneNo(userBook.getAddressZoneNo())
                .latitude(userBook.getLocation().getLat())
                .longitude(userBook.getLocation().getLon())
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
