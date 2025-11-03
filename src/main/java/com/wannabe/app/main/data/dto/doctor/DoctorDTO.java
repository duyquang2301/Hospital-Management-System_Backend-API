package com.wannabe.app.main.data.dto.doctor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
    @Schema(description = "의사 아이디")
    private Long id;

    @Schema(description = "병원 아이디")
    private Long hospitalId;

    @Schema(description = "의사 이름")
    private String name;

    @Schema(description = "의사 진료 과목")
    private List<String> categories;

    @Schema(description = "직위")
    private String position;

    @Schema(description = "의사 소개")
    private String description;

    @Schema(description = "의사 등록일")
    private LocalDateTime createAt;

    @Schema(description = "의사 삭제일")
    private LocalDateTime deletedAt;

    @Schema(description = "의사 상태")
    private String status;

    @Schema(description = "의사 프로필 이미지 아이디")
    private Long imageGroupId;

    @Schema(description = "의사 프로필 이미지")
    private String profileImg;
}
