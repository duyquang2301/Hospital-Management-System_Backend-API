package com.wannabe.app.main.data.dto.auth;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WannabeToken {

    public final static String ATTRIBUTE_KEY = "WannabeToken";
    private long userId;
    private Date expirationTime;
    private Date dateCreated;
    private String token;

    public void updateExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void updateTokenString(String token) {
        this.token = token;
    }
}
