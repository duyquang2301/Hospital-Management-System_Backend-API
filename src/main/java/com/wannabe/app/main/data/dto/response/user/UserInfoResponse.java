package com.wannabe.app.main.data.dto.response.user;

import com.wannabe.app.main.data.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private Long notificationCount;
    private String profileImg;
    private String nickname;
    private Integer totalPoint;

    public UserInfoResponse(User user) {
        this.profileImg = user.getProfilePath();
        this.nickname = user.getNickname();
        this.totalPoint = user.getPoint();
    }
}
