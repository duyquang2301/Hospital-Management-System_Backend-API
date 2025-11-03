package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum BannerType {

    BANNER("BANNER"),
    TERM("TERM"),
    NOTICE("NOTICE"),
    ANNOUNCEMENT("ANNOUNCEMENT"),
    MAIN_BANNER("MAIN_BANNER");

    private final String bannerType;

    BannerType(String bannerType) {
        this.bannerType = bannerType;
    }
}
