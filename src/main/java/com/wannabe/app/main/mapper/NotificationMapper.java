package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.entity.Notification;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper {

    List<Notification> findNotificationListByUserId(long userId);
    void insertNotification(Notification notification);
    void updateNotificationReadByUserId(long userId);
}
