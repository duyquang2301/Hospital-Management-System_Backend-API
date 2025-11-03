package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum ReviewType {

    NORMAL("NORMAL"),
    EVENT("EVENT"),
    VIRTUAL("VIRTUAL");

    private final String reviewTypeValue;

    ReviewType(String reviewTypeValue) {
        this.reviewTypeValue = reviewTypeValue;
    }
}
