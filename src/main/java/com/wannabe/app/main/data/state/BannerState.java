package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum BannerState {

    ACTIVE("ACTIVE"),
    PENDING("PENDING"),
    DELETED("DELETED");

    private final String state;

    BannerState(String state) {
        this.state = state;
    }
}
