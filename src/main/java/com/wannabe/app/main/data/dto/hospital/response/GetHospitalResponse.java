package com.wannabe.app.main.data.dto.hospital.response;

import com.wannabe.app.main.data.dto.common.CommonDto.HospitalInfo;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.Event;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetSearchHospitalsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetHospitalResponse {

    @Schema(description = "병원 정보")
    private HospitalInfo hospitalInfo;

    @Schema(description = "병원 상담횟수")
    private String counselCount;

    @Schema(description = "이벤트 내용")
    private List<Event> events;

    public static GetHospitalResponse from(GetSearchHospitalsDto hospitalInfo) {
        return new GetHospitalResponse(
            hospitalInfo.getHospitalInfo(),
            hospitalInfo.getCounselCount(),
            hospitalInfo.getEvents()
        );
    }
}
