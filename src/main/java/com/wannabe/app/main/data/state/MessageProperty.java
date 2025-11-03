package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum MessageProperty {

    SEND_USER("sendUser"),
    IS_READ("isRead");

    private final String propertyName;

    MessageProperty(String propertyName) {
        this.propertyName = propertyName;
    }
}
