package com.wannabe.app.main.data.dto.response.auth;

import com.wannabe.app.main.data.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private Long userId;
    private String state;
    private String token;
    private String refreshToken;

    public LoginResponse (User user, String token, String refreshToken) {
        this.userId = user.getId();
        this.state = user.getState();
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
