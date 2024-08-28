package com.example.project.user.client.dto;


import com.example.project.common.errorHandling.customRuntimeException.RuntimeExceptionWithCode;
import com.example.project.common.errorHandling.errorEnums.GlobalErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoAddressSearchDto {

    private Meta meta;
    private List<Documents> documents;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private Long total_count;
        private Long pageable_count;
        private Boolean is_end;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Documents {
        private String address_name;
        private String x;
        private String y;
        private String address_type;
        private AddressDto address;
        private RoadAddressDto road_address;

    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressDto {
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String h_code;
        private String b_code;
        private String mountain_yn;
        private String main_address_no;
        private String sub_address_no;
        private String y;
        private String x;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoadAddressDto {
        private String address_name;
        private String region_1depth_name;
        private String region_2depth_name;
        private String region_3depth_name;
        private String road_name;
        private String underground_yn;
        private String main_building_no;
        private String sub_building_no;
        private String building_name;
        private String zone_no;
        private String y;
        private String x;
    }

    public Documents getSameZoneNoFromDoc(String zoneNo, String addressName) {
        for (Documents d : this.documents
                ) {
            if(d.getRoad_address().zone_no.equals(zoneNo) && d.getRoad_address().address_name.equals(addressName)) {
                return d;
            }
        }
        throw new RuntimeExceptionWithCode(GlobalErrorCode.BAD_REQUEST, "cant not find address");
    }
}
