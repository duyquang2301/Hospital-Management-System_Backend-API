package com.wannabe.app.main.data.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserChatProfileDTO {

    private Long userId;
    private String profileImageUrl;
    private String nickname;

    public void updateSignedUrl(String signedUrl) {
        this.profileImageUrl = signedUrl;
    }
}
