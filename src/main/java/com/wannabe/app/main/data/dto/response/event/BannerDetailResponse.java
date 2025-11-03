package com.wannabe.app.main.data.dto.response.event;

import com.wannabe.app.main.data.dto.home.BannerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BannerDetailResponse {

    @Schema(description = "배너 아이디")
    private Long id;

    @Schema(description = "배너 이미지")
    private String image;

    @Schema(description = "연겵 이미지")
    private String image2;

    @Schema(description = "연겵 여부")
    private String bannerLinkYn;

    @Schema(description = "병원 id")
    private Long hospitalId;

}
