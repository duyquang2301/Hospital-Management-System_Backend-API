package com.wannabe.app.main.data.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginFilter {

    private String loginKey;
    private String loginType;
}
