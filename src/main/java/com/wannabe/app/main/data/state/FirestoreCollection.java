package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum FirestoreCollection {
    CHAT("chat"),
    USER("user"),
    MESSAGES("messages");

    private final String collectionName;

    FirestoreCollection(String collectionName) {
        this.collectionName = collectionName;
    }
}
