package com.wannabe.app.main.data.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Bookmark {

    private long userId;
    private long typeId;
    private String type;
    private LocalDateTime createdAt;

    public Bookmark(long userId, long typeId, String type) {
        this.userId = userId;
        this.typeId = typeId;
        this.type = type;
    }

    public static Bookmark of(long userId, long typeId, String type) {
        return new Bookmark(
            userId, typeId, type
        );
    }
}
