package com.wannabe.app.main.data.dto.user.response;

import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.user.MyEventCounselDto;
import com.wannabe.app.main.data.state.CounselState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyEventCounselResponse {

    @Schema(description = "상담 아이디")
    private final Long id;

    @Schema(description = "이벤트 아이디")
    private final Long eventId;

    @Schema(description = "이벤트 썸네일")
    private final String thumbNail;

    @Schema(description = "이벤트 지역")
    private final Region region;

    @Schema(description = "이벤트 병원 이름")
    private final String hospitalName;

    @Schema(description = "이벤트 이름")
    private final String eventName;

    @Schema(description = "상담 신청 일")
    private final LocalDateTime createAt;

    @Schema(description = "상담 상태")
    private final CounselState state;

    @Schema(description = "신청자 닉네임")
    private final String nickName;

    @Schema(description = "신청자 전화 번호")
    private final String phoneNumber;

    @Schema(description = "상담 내역")
    private final String content;

    @Schema(description = "답변 내역")
    private final String answer;

    public static MyEventCounselResponse from(MyEventCounselDto eventCounsel) {
        return new MyEventCounselResponse(
            eventCounsel.getId(),
            eventCounsel.getEventId(),
            eventCounsel.getThumbNail(),
            eventCounsel.getRegion(),
            eventCounsel.getHospitalName(),
            eventCounsel.getEventName(),
            eventCounsel.getCreateAt(),
            eventCounsel.getState(),
            eventCounsel.getNickName(),
            eventCounsel.getPhoneNumber(),
            eventCounsel.getContent(),
            eventCounsel.getAnswer()
        );
    }
}
