package com.wannabe.app.main.data.dto.virutality.response;

import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.user.MyCounselDto;
import com.wannabe.app.main.data.state.CounselState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCounselHospitalListResponse {

    @Schema(description = "상담 아이디")
    private final Long id;

    @Schema(description = "병원 아이디")
    private final Long hospitalId;

    @Schema(description = "병원 썸네일")
    private final String thumbNail;

    @Schema(description = "지역")
    private final Region region;

    @Schema(description = "병원 이름")
    private final String hospitalName;

    @Schema(description = "상담 상태")
    private final CounselState state;

    @Schema(description = "상담 신청 날짜")
    private final LocalDateTime createAt;

    public static MyCounselHospitalListResponse from(MyCounselDto myCounselDto) {
        return new MyCounselHospitalListResponse(
            myCounselDto.getId(),
            myCounselDto.getHospitalId(),
            myCounselDto.getThumbNail(),
            myCounselDto.getRegion(),
            myCounselDto.getHospitalName(),
            myCounselDto.getState(),
            myCounselDto.getCreatedAt()
        );
    }

}
