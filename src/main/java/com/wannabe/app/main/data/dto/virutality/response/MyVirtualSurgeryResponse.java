package com.wannabe.app.main.data.dto.virutality.response;

import com.wannabe.app.main.data.dto.virutality.MyvirtualSurgeryDto;
import com.wannabe.app.main.data.state.VirtualSurgeryType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyVirtualSurgeryResponse {

    @Schema(description = "가상 성형 아이디")
    private final Long id;

    @Schema(description = "가상 성형 전 이미지")
    private final String beforeImage;

    @Schema(description = "가상 성형 후 이미지")
    private final String afterImage;

    @Schema(description = "가상 성형 오른쪽 촬영 사진")
    private final String rightImage;

    @Schema(description = "가상 성형 왼쪽 촬영 사진")
    private final String leftImage;

    @Schema(description = "가상 성형 생성일")
    private final LocalDateTime createAt;

    @Schema(description = "가상 성형 타입")
    private final VirtualSurgeryType type;

    public static MyVirtualSurgeryResponse from(MyvirtualSurgeryDto myvirtualSurgeryDto) {
        return new MyVirtualSurgeryResponse(
            myvirtualSurgeryDto.getId(),
            myvirtualSurgeryDto.getBeforeImage(),
            myvirtualSurgeryDto.getAfterImage(),
            myvirtualSurgeryDto.getRightImage(),
            myvirtualSurgeryDto.getLeftImage(),
            myvirtualSurgeryDto.getCreateAt(),
            myvirtualSurgeryDto.getType()
        );
    }
}
