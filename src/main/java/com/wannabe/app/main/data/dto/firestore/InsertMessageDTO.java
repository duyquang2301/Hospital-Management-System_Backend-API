package com.wannabe.app.main.data.dto.firestore;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InsertMessageDTO {

    private String content;
    private Long sendUser;
    private Date createdAt;
    private Boolean isRead;

    public static InsertMessageDTO of(String content, Long sendUser) {
        return new InsertMessageDTO(content, sendUser);
    }

    private InsertMessageDTO(String content, Long sendUser) {
        this.content = content;
        this.sendUser = sendUser;
        this.createdAt = new Date();
        this.isRead = false;
    }
}
