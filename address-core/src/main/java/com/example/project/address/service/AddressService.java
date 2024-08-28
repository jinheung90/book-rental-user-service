package com.example.project.address.service;

import com.example.project.address.client.dto.KakaoAddressSearchDto;
import com.example.project.address.entity.RoadAddress;
import com.example.project.address.repository.RoadAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final RoadAddressRepository roadAddressRepository;

    public RoadAddress saveRoadAddress(KakaoAddressSearchDto.RoadAddressDto roadAddressDto) {
        return roadAddressRepository.save(
            RoadAddress.builder()
                .addressName(roadAddressDto.getAddress_name())
                .x(Double.parseDouble(roadAddressDto.getX()))
                .y(Double.parseDouble(roadAddressDto.getY()))
                .buildingName(roadAddressDto.getBuilding_name())
                .roadName(roadAddressDto.getRoad_name())
                .mainBuildingNo(roadAddressDto.getMain_building_no())
                .region1depthName(roadAddressDto.getRegion_1depth_name())
                .region2depthName(roadAddressDto.getRegion_2depth_name())
                .region3depthName(roadAddressDto.getRegion_3depth_name())
                .undergroundYn(roadAddressDto.getUnderground_yn())
                .zoneNo(roadAddressDto.getZone_no())
                .subBuildingNo(roadAddressDto.getSub_building_no())
                .build()
        );
    }
}
