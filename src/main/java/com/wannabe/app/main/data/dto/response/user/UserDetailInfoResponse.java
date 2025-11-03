package com.wannabe.app.main.data.dto.response.user;

import com.wannabe.app.main.data.entity.User;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailInfoResponse {
    private String profileImg;
    private String nickname;
    private String name;
    private String phoneNum;
    private String birth;
    private String gender;
    private String loginEmail;

    public UserDetailInfoResponse (User user) {
        this.profileImg = user.getProfilePath();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.phoneNum = user.getPhoneNumber();
        this.birth = user.getDateBirth();
        this.gender = user.getGender();
        this.loginEmail = user.getLoginEmail();
    }
}
