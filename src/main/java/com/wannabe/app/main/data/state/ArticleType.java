package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum ArticleType {

    ARTICLE("ARTICLE"),
    REVIEW("REVIEW");

    private final String articleTypeValue;

    ArticleType(String reviewTypeValue) {
        this.articleTypeValue = reviewTypeValue;
    }
}
