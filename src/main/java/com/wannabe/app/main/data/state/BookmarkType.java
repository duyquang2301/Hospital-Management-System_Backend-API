package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum BookmarkType {

    HOSPITAL("HOSPITAL"),
    EVENT("EVENT"),
    ARTICLE("ARTICLE");

    private final String bookmarkType;

    BookmarkType(String bookmarkType) {
        this.bookmarkType = bookmarkType;
    }
}
