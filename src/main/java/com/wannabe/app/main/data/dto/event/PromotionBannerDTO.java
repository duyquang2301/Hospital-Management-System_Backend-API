package com.wannabe.app.main.data.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionBannerDTO {

    private Long id;
    private String path;

    public static PromotionBannerDTO of() {
        return new PromotionBannerDTO();
    }

    public void updateSignedUrl(String url) {
        this.path = url;
    }
}
