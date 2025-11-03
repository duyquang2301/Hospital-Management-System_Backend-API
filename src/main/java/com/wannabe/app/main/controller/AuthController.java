package com.wannabe.app.main.controller;

import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.annotation.RefreshCallable;
import com.wannabe.app.main.data.dto.request.auth.LoginRequest;
import com.wannabe.app.main.data.dto.request.user.DeviceTokenRequest;
import com.wannabe.app.main.data.dto.response.auth.LoginResponse;
import com.wannabe.app.main.data.dto.response.auth.ValidationUserResponse;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인")
    @AnonymousCallable
    public Callable<Response<LoginResponse>> login(@RequestBody LoginRequest request) {
        return () -> new Response<>(authService.login(request));
    }

    @PostMapping("")
    @Operation(summary = "회원 조회")
    @AnonymousCallable
    public Callable<Response<ValidationUserResponse>> validateUser(@RequestBody LoginRequest request) {
        return () -> new Response<>(new ValidationUserResponse(authService.validateUser(request.getLoginKey(), request.getLoginType())));
    }

    @PutMapping("/token/refreshment")
    @Operation(summary = "토큰 재발급")
    @RefreshCallable
    public Callable<Response<LoginResponse>> refreshToken(HttpServletRequest request) {
        return () -> new Response<>(authService.refreshToken(request));
    }

    @PutMapping("/validation/login")
    @Operation(summary = "디바이스 토큰 검증(로그인 하기 전 화면 분기 처리 시 사용)")
    public Callable<Response<LoginResponse>> validateLogin(@RequestBody DeviceTokenRequest deviceTokenRequest, HttpServletRequest request) {
        return () -> new Response<>(authService.validateLogin(deviceTokenRequest.getDeviceToken(), request));
    }

    @PutMapping("/logout")
    @Operation(summary = "로그아웃")
    public Response<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return Response.ok();
    }
}
