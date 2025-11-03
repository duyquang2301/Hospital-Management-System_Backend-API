package com.wannabe.app.main.data.dto.banner;

import com.wannabe.app.main.data.dto.home.BannerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class BannerResponse {

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

    public static BannerResponse from(BannerDto banner) {
        return new BannerResponse(banner.getId(), banner.getUrl(), banner.getUrl2(), banner.getBannerLinkYn(), banner.getHospitalId());
    }

}
