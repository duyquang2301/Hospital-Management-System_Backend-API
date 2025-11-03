package com.wannabe.app.main.controller;

import static com.wannabe.app.main.utility.constant.HeaderKey.USER_ID;

import com.wannabe.app.main.data.dto.firestore.request.FirstChatRequest;
import com.wannabe.app.main.data.dto.firestore.request.MessageRequest;
import com.wannabe.app.main.data.dto.firestore.response.ChattingListResponse;
import com.wannabe.app.main.data.dto.firestore.response.ChattingResponse;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.ChattingService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/chatting")
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chattingService;

    @PostMapping("")
    @Operation(summary = "채팅방 생성 요청, 채팅방이 있을 경우 존재하는 채팅방 응답")
    public Callable<Response<ChattingResponse>> createChatRoom(
        @RequestAttribute(USER_ID) Long userId,
        @RequestBody FirstChatRequest request) {
        return () -> Response.of(chattingService.createChatRoom(userId, request));
    }

    @GetMapping("list")
    @Operation(summary = "채팅방 리스트 조회")
    public Callable<Response<ChattingListResponse>> getChattingList(
        @RequestAttribute(USER_ID) Long userId) {
        return () -> Response.of(ChattingListResponse.from(chattingService.getChattingList(userId)));
    }

    @PostMapping("block/{targetUserId}")
    @Operation(summary = "채팅방 차단")
    public Callable<Response<Void>> blockUser(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable long targetUserId) {
        chattingService.blockUser(userId, targetUserId);
        return Response::ok;
    }

    @DeleteMapping("block/{targetUserId}")
    @Operation(summary = "채팅방 차단 해제")
    public Callable<Response<Void>> unblockUser(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable long targetUserId) {
        chattingService.unblockUser(userId, targetUserId);
        return Response::ok;
    }

    @PutMapping("{chatId}/message")
    @Operation(summary = "채팅방 메시지 전송")
    public Callable<Response<Void>> sendMessage(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable long chatId,
        @RequestBody MessageRequest request) {
        chattingService.sendMessage(userId, chatId, request.getMessage());
        return Response::ok;
    }

    @PutMapping("{chatId}/connect")
    @Operation(summary = "채팅방 연결")
    public Callable<Response<Void>> connectChatRoom(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable long chatId) {
        chattingService.connectChatRoom(userId, chatId);
        return Response::ok;
    }

    @DeleteMapping("{chatId}/connect")
    @Operation(summary = "채팅방 연결 해제")
    public Callable<Response<Void>> disconnectChatRoom(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable long chatId) {
        chattingService.disconnectChatRoom(userId, chatId);
        return Response::ok;
    }
}
