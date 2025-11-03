package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum CounselState {
    PROGRESS("PROGRESS"),
    COMPLETED("COMPLETED");

    private final String state;

    CounselState(String state) {
        this.state = state;
    }
}
