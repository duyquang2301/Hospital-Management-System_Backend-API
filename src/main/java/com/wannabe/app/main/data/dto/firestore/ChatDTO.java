package com.wannabe.app.main.data.dto.firestore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {

    private Date createdAt;
    private LastMessageDTO lastMessage;
    private List<Long> users;
    private List<Long> requestBlock;

    public static ChatDTO from(LastMessageDTO lastMessage, List<Long> users) {
        return new ChatDTO(lastMessage, users);
    }

    public void addNewRequestBlock(long userId) {
        this.requestBlock = List.of(userId);
    }

    public void updateRequestBlock(long userId) {
        this.requestBlock.add(userId);
    }

    public void removeRequestBlock(long userId) {
        this.requestBlock.remove(userId);
    }

    public boolean isBlocked(long userId) {
        return this.requestBlock != null && this.requestBlock.contains(userId);
    }

    public long getOtherUserId(long userId) {
        return this.users.stream()
            .filter(id -> id != userId)
            .findFirst()
            .orElseThrow();
    }

    private ChatDTO (LastMessageDTO lastMessage, List<Long> users) {
        this.createdAt = new Date();
        this.lastMessage = lastMessage;
        this.users = users;
        this.requestBlock = new ArrayList<>();
    }
}
