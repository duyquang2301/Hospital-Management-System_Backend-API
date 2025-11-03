package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.firestore.response.ChattingResponse;
import com.wannabe.app.main.data.dto.notification.response.NotificationResponse;
import com.wannabe.app.main.data.entity.Event;
import com.wannabe.app.main.data.entity.Hospital;
import com.wannabe.app.main.data.entity.Notification;
import com.wannabe.app.main.mapper.EventMapper;
import com.wannabe.app.main.mapper.HospitalMapper;
import com.wannabe.app.main.mapper.NotificationMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService {

    private final ChattingService chattingService;

    private final NotificationMapper notificationMapper;
    private final EventMapper eventMapper;
    private final HospitalMapper hospitalMapper;

    /**
     * 댓글 알림 생성
     *
     * @param userId    사용자 아이디
     * @param articleId 게시글 아이디
     * @param commentId 댓글 아이디
     */
    public void createNotificationByComment(long userId, long articleId, long commentId) {
        notificationMapper.insertNotification(Notification.createComment(userId, articleId, commentId));
    }

    /**
     * 답글 알림 생성
     *
     * @param userId    사용자 아이디
     * @param articleId 게시글 아이디
     * @param commentId 답글 아이디
     */
    public void createNotificationByReComment(long userId, long articleId, long commentId) {
        notificationMapper.insertNotification(Notification.createReComment(userId, articleId, commentId));
    }

    /**
     * 알림 목록 조회
     *
     * @param userId 사용자 아아디
     * @return List<NotificationResponse> 알림 목록
     */
    public List<NotificationResponse> getNotificationList(long userId) {
        List<Notification> findList = notificationMapper.findNotificationListByUserId(userId);

        if (findList == null || findList.isEmpty()) {
            return new ArrayList<>();
        }

        List<NotificationResponse> result = new ArrayList<>();
        notificationMapper.updateNotificationReadByUserId(userId);

        for (Notification notification : findList) {
            addNotificationResponse(result, notification);
        }

        return result;
    }

    /**
     * 알림 개수 조회
     *
     * @param userId 사용자 아아디
     * @return long 알림 개수
     */
    public long getNotificationCount(long userId) {
        List<Notification> findList = notificationMapper.findNotificationListByUserId(userId);

        long count = 0;

        for (Notification notification : findList) {
            if (!notification.isRead()) {
                count++;
            }
        }

        long chattingCount = getUnreadCount(userId);

        return count + chattingCount;
    }

    /**
     * 읽지 않은 알림 개수 조회
     *
     * @param userId 사용자 조회
     * @return long 읽지 않은 알림 개스
     */
    private long getUnreadCount(long userId) {
        List<ChattingResponse> chattingList = chattingService.getChattingList(userId);

        if (chattingList == null || chattingList.isEmpty()) {
            return 0;
        }

        long count = 0;

        for (ChattingResponse chatting : chattingList) {
            if (chatting.getUnreadCount() == 0) {
                continue;
            }

            count = count + chatting.getUnreadCount();
        }

        return count;
    }

    /**
     * 알림 목록 정보 조회
     *
     * @param result       알림 목록 정보
     * @param notification 알림 정보
     * @return List<NotificationResponse> 알림 목록
     */
    private List<NotificationResponse> addNotificationResponse(List<NotificationResponse> result, Notification notification) {
        if (notification.isCommunityType()) {
            result.add(NotificationResponse.createByCommunity(notification));
            return result;
        }

        if (notification.isGetEventName()) {
            Event eventById = eventMapper.findEventById(notification.getDetailTypeId());

            if (eventById == null) {
                return result;
            }

            result.add(NotificationResponse.createByCounsel(notification, eventById.getName()));
        }

        Hospital hospital = hospitalMapper.getHospital(notification.getDetailTypeId());

        if (hospital == null) {
            return result;
        }

        result.add(NotificationResponse.createByCounsel(notification, hospital.getName()));
        return result;
    }
}
