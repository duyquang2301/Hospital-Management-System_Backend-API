package com.wannabe.app.main.data.dto.virutality.response;

import com.wannabe.app.main.data.dto.virutality.GetMyVirtualSurgeryDto;
import com.wannabe.app.main.data.state.CounselState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyVirtualSurgeryListResponse {

    @Schema(description = "가상 성형 아이디")
    private final Long id;

    @Schema(description = "가상 성형 썸네일")
    private final String thumbNail;

    @Schema(description = "가상 성형 생성일")
    private final LocalDateTime createAt;

    @Schema(description = "상담 상태")
    private final CounselState state;

    public static MyVirtualSurgeryListResponse from(GetMyVirtualSurgeryDto getMyVirtualSurgeryDto) {
        return new MyVirtualSurgeryListResponse(
            getMyVirtualSurgeryDto.getId(),
            getMyVirtualSurgeryDto.getThumbNail(),
            getMyVirtualSurgeryDto.getCreateAt(),
            getMyVirtualSurgeryDto.getState()
        );
    }

}
