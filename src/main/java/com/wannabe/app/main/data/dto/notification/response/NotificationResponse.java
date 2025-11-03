package com.wannabe.app.main.data.dto.notification.response;

import com.wannabe.app.main.data.entity.Notification;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long notificationId;
    private String title;
    private Long typeId;
    private String type;
    private String detailType;
    private LocalDateTime notificationDate;
    private Long detailTypeId;

    public static NotificationResponse createByCommunity(Notification notification) {
        return new NotificationResponse(notification);
    }

    public static NotificationResponse createByCounsel(Notification notification, String eventName) {
        return new NotificationResponse(notification, eventName);
    }

    private NotificationResponse (Notification communityNotification) {
        this.notificationId = communityNotification.getId();
        this.title = null;
        this.type = communityNotification.getType();
        this.typeId = communityNotification.getTypeId();
        this.detailType = communityNotification.getDetailType();
        this.detailTypeId = communityNotification.getDetailTypeId();
        this.notificationDate = communityNotification.getCreatedAt();
    }

    private NotificationResponse (Notification communityNotification, String title) {
        this.notificationId = communityNotification.getId();
        this.title = title;
        this.type = communityNotification.getType();
        this.typeId = communityNotification.getTypeId();
        this.detailType = communityNotification.getDetailType();
        this.detailTypeId = communityNotification.getDetailTypeId();
        this.notificationDate = communityNotification.getCreatedAt();
    }
}
