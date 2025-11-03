package com.wannabe.app.main.data.dto.home.response;

import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetRecommendListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PopularHospitalListResponse {

    @Schema(description = "병원 아이디")
    private Long id;

    @Schema(description = "병원 썸네일")
    private String thumbNail;

    @Schema(description = "병원 이름")
    private String name;

    @Schema(description = "병원 지역")
    private Region region;

    @Schema(description = "병원 상담 신청 수")
    private Integer counselCount;

    public static PopularHospitalListResponse from(GetRecommendListDto hospitalDto) {
        return new PopularHospitalListResponse(
            hospitalDto.getHospitalInfo().getId(),
            hospitalDto.getHospitalInfo().getThumbNail(),
            hospitalDto.getHospitalInfo().getName(),
            hospitalDto.getHospitalInfo().getRegion(),
            hospitalDto.getCounselCount()
        );
    }
}
