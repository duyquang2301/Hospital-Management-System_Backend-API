package com.wannabe.app.main.data.dto.notification.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationListResponse {

    private List<NotificationResponse> notificationList;

    public static NotificationListResponse of(List<NotificationResponse> notificationList) {
        return new NotificationListResponse(notificationList);
    }
}
