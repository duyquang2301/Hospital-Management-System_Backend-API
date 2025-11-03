package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum UserState {

    CREATED("CREATED"),
    JOINED("JOINED"),
    DELETED("DELETED");

    private final String userSateValue;

    UserState (String userStateValue) {
        this.userSateValue = userStateValue;
    }
}
