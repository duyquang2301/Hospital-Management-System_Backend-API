package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum NotificationType {

    COUNSEL("COUNSEL"),
    COMMUNITY("COMMUNITY");

    private final String type;

    NotificationType(String type) {
        this.type = type;
    }
}
