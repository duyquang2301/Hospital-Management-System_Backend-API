package com.wannabe.app.main.data.dto.event.response;

import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.event.GetEventDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventListResponse {

    @Schema(description = "이벤트 아이디")
    private Long id;

    @Schema(description = "이벤트 썸네일")
    private String thumbNail;

    @Schema(description = "이벤트 지역")
    private Region region;

    @Schema(description = "이벤트 병원 이름")
    private String hospitalName;

    @Schema(description = "이벤트 이름")
    private String name;

    @Schema(description = "이벤트 상담 횟수")
    private Integer counselCount;

    @Schema(description = "이벤트 가격")
    private Integer cost;

    public static EventListResponse from(GetEventDto eventDto) {
        return new EventListResponse(
            eventDto.getId(),
            eventDto.getThumbNail(),
            eventDto.getRegion(),
            eventDto.getHospitalName(),
            eventDto.getName(),
            eventDto.getCounselCount(),
            eventDto.getCost()
        );
    }

}
