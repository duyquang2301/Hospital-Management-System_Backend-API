package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum NotificationDetailType {

    HOSPITAL("HOSPITAL"),
    EVENT("EVENT"),
    VIRTUAL("VIRTUAL"),
    COMMENT("COMMENT"),
    RE_COMMENT("RE_COMMENT");

    private final String detailType;

    NotificationDetailType (String detailType) {
        this.detailType = detailType;
    }
}
