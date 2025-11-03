package com.wannabe.app.main.data.dto.firestore;

import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConnectDTO {

    private Long connectedChat;
    private Date connectedAt;

    public static ConnectDTO of(String firebaseKey) {
        return new ConnectDTO(Long.valueOf(firebaseKey), new Date());
    }

    public static ConnectDTO convert(Object connectedChat, Date connectedAt) {
        return new ConnectDTO(Long.valueOf(connectedChat.toString()), connectedAt);
    }
}
