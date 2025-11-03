package com.wannabe.app.main.data.dto.notification.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCountResponse {

    private Long notificationCount;

    public static NotificationCountResponse of(Long notificationCount) {
        return new NotificationCountResponse(notificationCount);
    }
}
