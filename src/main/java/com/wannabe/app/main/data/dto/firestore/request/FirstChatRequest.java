package com.wannabe.app.main.data.dto.firestore.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FirstChatRequest {

    private Long targetUserId;
    private String firstMessage;
}
