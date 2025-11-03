package com.wannabe.app.main.data.dto.user.response;

import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.user.MyHospitalCounselDto;
import com.wannabe.app.main.data.state.CounselState;
import com.wannabe.app.main.data.state.CounselType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCounselResponse {

    @Schema(description = "상담 신청 아이디")
    private final Long id;

    @Schema(description = "병원 아이디")
    private final Long hospitalId;

    @Schema(description = "썸네일")
    private final String thumbNail;

    @Schema(description = "이름")
    private final String name;

    @Schema(description = "지역")
    private final Region region;

    @Schema(description = "신청일")
    private final LocalDateTime createAt;

    @Schema(description = "신청 상태")
    private final CounselState state;

    @Schema(description = "신청자 닉네임")
    private final String nickName;

    @Schema(description = "신청자 전화번호")
    private final String phoneNumber;

    @Schema(description = "상담 내용")
    private final String content;

    @Schema(description = "답변 내역")
    private final String answer;

    @Schema(description = "상담 희망 부위")
    private final List<String> surgeryPart;

    @Schema(description = "병원 상담 이미지")
    private final List<String> image;

    @Schema(description = "가상성형 전 이미지")
    private final String beforeImage;

    @Schema(description = "가상성형 후 이미지")
    private final String afterImage;

    @Schema(description = "가상성형 좌 이미지")
    private final String leftImage;

    @Schema(description = "가상성형 우 이미지")
    private final String rightImage;

    @Schema(description = "가상 성형 타입")
    private final CounselType type;

    public static MyCounselResponse from(MyHospitalCounselDto myCounselDto) {
        return new MyCounselResponse(
            myCounselDto.getId(),
            myCounselDto.getHospitalId(),
            myCounselDto.getThumbNail(),
            myCounselDto.getName(),
            myCounselDto.getRegion(),
            myCounselDto.getCreateAt(),
            myCounselDto.getState(),
            myCounselDto.getNickName(),
            myCounselDto.getPhoneNumber(),
            myCounselDto.getContent(),
            myCounselDto.getAnswer(),
            myCounselDto.getSurgeryPart(),
            myCounselDto.getImage(),
            myCounselDto.getBeforeImage(),
            myCounselDto.getAfterImage(),
            myCounselDto.getLeftImage(),
            myCounselDto.getRightImage(),
            myCounselDto.getType()
        );
    }
}
