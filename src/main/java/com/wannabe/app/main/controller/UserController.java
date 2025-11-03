package com.wannabe.app.main.controller;

import static com.wannabe.app.main.utility.constant.HeaderKey.USER_ID;

import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.community.response.ArticleListResponse;
import com.wannabe.app.main.data.dto.community.response.CommunityAllContentListResponse;
import com.wannabe.app.main.data.dto.community.response.ReviewListResponse;
import com.wannabe.app.main.data.dto.event.response.EventListResponse;
import com.wannabe.app.main.data.dto.hospital.response.GetHospitalResponse;
import com.wannabe.app.main.data.dto.request.user.DeviceTokenRequest;
import com.wannabe.app.main.data.dto.request.user.JoinRequest;
import com.wannabe.app.main.data.dto.request.user.UpdateUserInfoRequest;
import com.wannabe.app.main.data.dto.request.user.UpdateUserRequest;
import com.wannabe.app.main.data.dto.response.auth.LoginResponse;
import com.wannabe.app.main.data.dto.response.user.NicknameValidationResponse;
import com.wannabe.app.main.data.dto.response.user.OtherUserInfoResponse;
import com.wannabe.app.main.data.dto.response.user.UserAdditionInfoResponse;
import com.wannabe.app.main.data.dto.response.user.UserDetailInfoResponse;
import com.wannabe.app.main.data.dto.response.user.UserInfoResponse;
import com.wannabe.app.main.data.dto.user.response.MyCounselListResponse;
import com.wannabe.app.main.data.dto.user.response.MyCounselResponse;
import com.wannabe.app.main.data.dto.user.response.MyEventCounselResponse;
import com.wannabe.app.main.data.dto.user.response.MyPointResponse;
import com.wannabe.app.main.response.ListResponseDto;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.AuthService;
import com.wannabe.app.main.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("")
    @AnonymousCallable
    public Callable<Response<LoginResponse>> join(@RequestBody JoinRequest joinRequest) {
        long userId = userService.joinUser(joinRequest);
        return () -> new Response<>(authService.login(userId));
    }

    @GetMapping("/my-page")
    @Operation(summary = "내 프로필 정보 가져오기")
    public Callable<Response<UserInfoResponse>> getProfile(HttpServletRequest request) {
        return () -> new Response<>(userService.getProfile(authService.getUserId(request)));
    }

    @GetMapping("/my-page/detail")
    @Operation(summary = "내 프로필 상세 정보 가져오기")
    public Callable<Response<UserDetailInfoResponse>> getProfileDetail(HttpServletRequest request) {
        return () -> new Response<>(userService.getProfileDetail(authService.getUserId(request)));
    }

    @GetMapping("/info")
    @Operation(summary = "내 추가 정보")
    public Callable<Response<UserAdditionInfoResponse>> getUserInfo(HttpServletRequest request) {
        return () -> new Response<>(userService.getUserAdditionInfo(authService.getUserId(request)));
    }

    @PutMapping("/info")
    @Operation(summary = "사용자 정보 수정")
    public Callable<Response<Void>> updateUserInfo(
        @RequestBody UpdateUserInfoRequest request,
        HttpServletRequest httpServletRequest) {
        userService.updateUserInfo(authService.getUserId(httpServletRequest), request);
        return Response::ok;
    }

    @DeleteMapping("/withdrawal")
    @Operation(summary = "회원 탈퇴")
    public Callable<Response<Void>> withdrawal(@RequestAttribute(USER_ID) Long userId) {
        return () -> {
            userService.withdrawal(userId);
            authService.expireToken(userId);
            return Response.ok();
        };
    }

    @GetMapping("/nickname/validation")
    @AnonymousCallable
    @Operation(summary = "닉네임 중복 체크")
    public Callable<Response<NicknameValidationResponse>> checkNickName(@RequestParam("nickname") String nickname) {
        return () -> new Response<>(new NicknameValidationResponse(userService.checkNickname(nickname)));
    }

    @GetMapping("/nickname/test")
    @AnonymousCallable
    @Operation(summary = "닉네임 유효성 체크 테스트")
    public boolean checkNickNameTest(@RequestParam("nickname") String nickname) {
        return userService.checkNicknameTest(nickname);
    }

    @PutMapping("")
    @Operation(summary = "회원 정보 수정")
    public Callable<Response<Void>> updateUserInfo(
        @RequestPart(value = "userInfo") UpdateUserRequest request,
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
        @RequestAttribute(USER_ID) Long userId
    ) {
        userService.updateUser(userId, request, profileImage);
        return Response::ok;
    }

    @PutMapping("/device")
    @Operation(summary = "디바이스 토큰 정보 수정")
    public Callable<Response<Void>> updateDeviceToken(
        @RequestBody DeviceTokenRequest tokenRequest,
        HttpServletRequest request) {
        userService.updateDeviceToken(tokenRequest.getDeviceToken(), authService.getUserId(request));
        return Response::ok;
    }

    @GetMapping(value = "/bookmark/event")
    @Operation(summary = "이벤트 스크랩 목록")
    public Callable<Response<ListResponseDto<EventListResponse>>> getEventBookMarks(
        @RequestAttribute(USER_ID) Long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<EventListResponse> eventBookmarks = userService.getAllEventBookMarks(userId, page, size)
                .map(EventListResponse::from);
            return Response.of(eventBookmarks);
        };
    }

    @GetMapping(value = "/bookmark/hospital")
    @Operation(summary = "병원 스크랩 목록")
    public Callable<Response<ListResponseDto<GetHospitalResponse>>> getHospitalBookMarks(
        @RequestAttribute(USER_ID) Long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<GetHospitalResponse> hospitalBookmarks = userService.getAllHospitalBookMarks(userId, page, size)
                .map(GetHospitalResponse::from);
            return Response.of(hospitalBookmarks);
        };
    }

    @GetMapping(value = "/bookmark/community")
    @Operation(summary = "게시글 스크랩 목록")
    public Callable<Response<ListResponseDto<CommunityAllContentListResponse>>> getCommunityBookMarks(
        @RequestAttribute(USER_ID) Long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<CommunityAllContentListResponse> communityBookmarks = userService.getAllCommunityBookMarks(userId, page, size)
                .map(CommunityAllContentListResponse::from);
            return Response.of(communityBookmarks);
        };
    }

    @GetMapping("/info/{otherUserId}")
    @Operation(summary = "회원 정보 조회")
    public Callable<Response<OtherUserInfoResponse>> getOtherUserInfo(
        @PathVariable long otherUserId,
        @RequestAttribute(USER_ID) Long myUserId
    ) {
        return () -> {
            OtherUserInfoResponse userInfo = userService.getOtherUserInfo(myUserId, otherUserId);
            return Response.of(userInfo);
        };
    }

    @GetMapping("/{userId}/community")
    @Operation(summary = "회원이 작성한 모든 커뮤니티 글 목록 조회")
    public Callable<Response<ListResponseDto<CommunityAllContentListResponse>>> getUserAllContents(
        @RequestAttribute(USER_ID) Long myUserId,
        @PathVariable long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<CommunityAllContentListResponse> allContent = userService.getUserAllContents(page, size, userId, myUserId)
                .map(CommunityAllContentListResponse::from);
            return Response.of(allContent);
        };
    }

    @GetMapping("/{userId}/articles")
    @Operation(summary = "회원이 작성한 일반 게시글 목록 조회")
    public Callable<Response<ListResponseDto<ArticleListResponse>>> getUserArticles(
        @RequestAttribute(USER_ID) Long myUserId,
        @PathVariable long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<ArticleListResponse> articleList = userService.getUserArticles(page, size, userId, myUserId)
                .map(ArticleListResponse::from);
            return Response.of(articleList);
        };
    }

    @GetMapping("/{userId}/reviews")
    @Operation(summary = "회원이 작성한 시술 후기 게시글 목록 조회")
    public Callable<Response<ListResponseDto<ReviewListResponse>>> getUserReviews(
        @RequestAttribute(USER_ID) Long myUserId,
        @PathVariable long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<ReviewListResponse> reviewList = userService.getUserReviews(page, size, userId, myUserId).map(ReviewListResponse::from);
            return Response.of(reviewList);
        };
    }

    @GetMapping("/point")
    @Operation(summary = "회원 포인트 내역")
    public Callable<Response<MyPointResponse>> getPoint(@RequestAttribute(USER_ID) Long userId) {
        MyPointResponse myPoint = MyPointResponse.from(userService.getPoint(userId));
        return () -> Response.of(myPoint);
    }

    @GetMapping("/counsel/histories")
    @Operation(summary = "내 상담 신청 내역 조회")
    public Callable<Response<ListResponseDto<MyCounselListResponse>>> getCounsels(
        @RequestAttribute(USER_ID) Long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<MyCounselListResponse> myCounselList = userService.getAllCounselList(userId, page, size).map(MyCounselListResponse::from);
            return Response.of(myCounselList);
        };
    }

    @GetMapping("/event/counsel/{counselId}")
    @Operation(summary = "나의 이벤트 상담 내역 단일 조회")
    public Callable<Response<MyEventCounselResponse>> getEventCounsel(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable Long counselId
    ) {
        return () -> {
            MyEventCounselResponse myEventCounsel = MyEventCounselResponse.from(userService.getEventCounselDetail(userId, counselId));
            return Response.of(myEventCounsel);
        };
    }

    @GetMapping("/hospital/counsel/{counselId}")
    @Operation(summary = "나의 병원, 가상 성형 상담 내역 단일 조회")
    public Callable<Response<MyCounselResponse>> getCounsel(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable Long counselId
    ) {
        return () -> {
            MyCounselResponse myCounsel = MyCounselResponse.from(userService.getCounsel(userId, counselId));
            return Response.of(myCounsel);
        };
    }

}
