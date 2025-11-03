package com.wannabe.app.main.data.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Chatting {

    private Long id;
    private Long userId;
    private Long subjectUserId;
    private String firebaseKey;
    private LocalDateTime createdAt;

    public static Chatting from(Long id, Long userId, Long subjectUserId) {
        return new Chatting(id, userId, subjectUserId);
    }

    public long getOtherUserId(long userId) {
        return this.userId.equals(userId) ? subjectUserId : this.userId;
    }

    public boolean isChattingUser(long userId) {
        return this.userId.equals(userId) || subjectUserId.equals(userId);
    }

    private Chatting (Long id, Long userId, Long subjectUserId) {
        this.id = id;
        this.userId = userId;
        this.subjectUserId = subjectUserId;
        this.firebaseKey = id.toString();
        this.createdAt = LocalDateTime.now();
    }
}
