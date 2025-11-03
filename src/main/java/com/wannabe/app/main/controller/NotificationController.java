package com.wannabe.app.main.controller;

import static com.wannabe.app.main.utility.constant.HeaderKey.USER_ID;

import com.wannabe.app.main.data.dto.notification.response.NotificationCountResponse;
import com.wannabe.app.main.data.dto.notification.response.NotificationListResponse;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    @Operation(summary = "알림 내역 목록 조회")
    public Callable<Response<NotificationListResponse>> getNotificationList(
        @RequestAttribute(USER_ID) Long userId
    ) {
        return () -> Response.of(NotificationListResponse.of(notificationService.getNotificationList(userId)));
    }

    @GetMapping("")
    @Operation(summary = "알림 수 조회")
    public Callable<Response<NotificationCountResponse>> getNotificationCount(
        @RequestAttribute(USER_ID) Long userId
    ) {
        return () -> Response.of(NotificationCountResponse.of(notificationService.getNotificationCount(userId)));
    }
}
