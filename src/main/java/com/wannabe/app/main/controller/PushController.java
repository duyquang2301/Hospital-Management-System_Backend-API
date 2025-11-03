package com.wannabe.app.main.controller;

import com.wannabe.app.main.annotation.AdminCallable;
import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.request.push.CounselPushRequest;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.PushService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/push")
public class PushController {

    private final PushService pushService;

    @PostMapping("/counsel/hospital")
    @Operation(summary = "병원 상담 답변 시 푸시 메세지 발송 요청")
    @AnonymousCallable
    public Callable<Response<Void>> sendHospitalCounselPush(
        @RequestBody CounselPushRequest request
    ) {
        pushService.sendHospitalCounselPush(request.getUserId(), request.getCounselId(), request.getTitle());
        return Response::new;
    }

    @PostMapping("/counsel/event")
    @Operation(summary = "이벤트 상담 답변 시 푸시 메세지 발송 요청")
    @AnonymousCallable
    public Callable<Response<Void>> sendEventCounselPush(
        @RequestBody CounselPushRequest request
    ) {
        pushService.sendEventCounselPush(request.getUserId(), request.getCounselId(), request.getTitle());
        return Response::new;
    }

    @GetMapping("/admin/call/test")
    @Operation(summary = "관리자 토큰 검증 테스트")
    @AdminCallable
    public Callable<String> adminCallTest(HttpServletRequest request) {
        return request.getHeader("Authorization")::toString;
    }
}
