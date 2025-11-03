package com.wannabe.app.main.data.dto.response.user;

import com.wannabe.app.main.data.dto.user.UserChatProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OtherUserInfoResponse {

    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String chatId;

    public static OtherUserInfoResponse from(UserChatProfileDTO userChatProfileDTO) {
        return new OtherUserInfoResponse(userChatProfileDTO);
    }

    public static OtherUserInfoResponse of(UserChatProfileDTO userChatProfileDTO, String chatId) {
        return new OtherUserInfoResponse(userChatProfileDTO, chatId);
    }

    private OtherUserInfoResponse(UserChatProfileDTO userChatProfileDTO) {
        this.userId = userChatProfileDTO.getUserId();
        this.nickname = userChatProfileDTO.getNickname();
        this.profileImageUrl = userChatProfileDTO.getProfileImageUrl();
        this.chatId = null;
    }

    private OtherUserInfoResponse(UserChatProfileDTO userChatProfileDTO, String chatId) {
        this.userId = userChatProfileDTO.getUserId();
        this.nickname = userChatProfileDTO.getNickname();
        this.profileImageUrl = userChatProfileDTO.getProfileImageUrl();
        this.chatId = chatId;
    }
}
