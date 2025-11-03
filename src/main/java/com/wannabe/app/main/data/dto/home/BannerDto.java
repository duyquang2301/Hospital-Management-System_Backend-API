package com.wannabe.app.main.data.dto.home;

import com.wannabe.app.main.data.entity.Banner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BannerDto {

    private Long id;
    private String url;
    private String url2;
    private String bannerLinkYn;
    private Long hospitalId;

    public static BannerDto of(Banner banner) {
        return new BannerDto(banner.getId(),
            banner.getUrl(),
            banner.getUrl2(),
            banner.getBannerLinkYn(),
            banner.getHospitalId()
        );
    }
}
