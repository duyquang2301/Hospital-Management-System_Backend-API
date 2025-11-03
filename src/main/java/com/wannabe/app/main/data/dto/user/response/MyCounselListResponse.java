package com.wannabe.app.main.data.dto.user.response;

import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.user.MyCounselDto;
import com.wannabe.app.main.data.state.CounselState;
import com.wannabe.app.main.data.state.CounselType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCounselListResponse {

    @Schema(description = "상담 아이디")
    private final Long id;

    @Schema(description = "상담 썸네일")
    private final String thumbNail;

    @Schema(description = "지역")
    private final Region region;

    @Schema(description = "병원 이름")
    private final String hospitalName;

    @Schema(description = "이벤트 이름")
    private final String eventName;

    @Schema(description = "상담 타입")
    private final CounselType type;

    @Schema(description = "상담 상태")
    private final CounselState state;

    @Schema(description = "상담 신청 날짜")
    private LocalDateTime createAt;

    public static MyCounselListResponse from(MyCounselDto myCounsel) {
        return new MyCounselListResponse(
            myCounsel.getId(),
            myCounsel.getThumbNail(),
            myCounsel.getRegion(),
            myCounsel.getHospitalName(),
            myCounsel.getEventName(),
            myCounsel.getType(),
            myCounsel.getState(),
            myCounsel.getCreatedAt()
        );
    }

}
