package com.wannabe.app.main.data.dto.firestore;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LastMessageDTO {

    private String content;
    private Long sendUser;
    private Date createdAt;

    public static LastMessageDTO of(String content, Long sendUser) {
        return new LastMessageDTO(content, sendUser);
    }

    private LastMessageDTO(String content, Long sendUser) {
        this.content = content;
        this.sendUser = sendUser;
        this.createdAt = new Date();
    }
}
