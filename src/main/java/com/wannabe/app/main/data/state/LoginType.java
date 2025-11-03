package com.wannabe.app.main.data.state;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum LoginType {
    KAKAO("KAKAO"),
    LINE("LINE"),
    APPLE("APPLE"),
    GOOGLE("GOOGLE"),
    APP("APP");

    private final String loginTypeName;

    LoginType(String loginTypeName) {
        this.loginTypeName = loginTypeName;
    }

    public List<String> getLoginTypes() {
        return Arrays.stream(LoginType.values())
                .map(LoginType::getLoginTypeName)
                .toList();
    }
}
