package com.wannabe.app.main.data.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Block {

    private Long id;
    private Long userId;
    private Long subjectUserId;
    private LocalDateTime createdAt;

    public static Block from(Long userId, Long subjectUserId) {
        return new Block(userId, subjectUserId);
    }

    private Block(Long userId, Long subjectUserId) {
        this.userId = userId;
        this.subjectUserId = subjectUserId;
    }
}
