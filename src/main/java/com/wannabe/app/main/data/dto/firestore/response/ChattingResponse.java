package com.wannabe.app.main.data.dto.firestore.response;

import com.wannabe.app.main.data.dto.firestore.ChatDTO;
import com.wannabe.app.main.data.dto.firestore.LastMessageDTO;
import com.wannabe.app.main.data.dto.user.UserChatProfileDTO;
import com.wannabe.app.main.data.entity.Chatting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChattingResponse {

    private String firebaseKey;
    private LastMessageDTO lastMessage;
    private UserChatProfileDTO otherUserProfile;
    private Long unreadCount;

    public static ChattingResponse from(Chatting chatting, LastMessageDTO lastMessageDTO, UserChatProfileDTO userProfile) {
        return new ChattingResponse(chatting.getFirebaseKey(), lastMessageDTO, userProfile, 0L);
    }

    public static ChattingResponse from(Chatting chatEntity, ChatDTO chatDTO, UserChatProfileDTO otherUser, long unreadCount) {
        return new ChattingResponse(chatEntity.getFirebaseKey(), chatDTO.getLastMessage(), otherUser, unreadCount);
    }
}
