package com.wannabe.app.main.controller;

import static com.wannabe.app.main.utility.constant.HeaderKey.USER_ID;

import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.request.virtuality.CreateVirtualAndroidRequest;
import com.wannabe.app.main.data.dto.request.virtuality.CreateVirtualRequest;
import com.wannabe.app.main.data.dto.request.virtuality.VirtualCategoryRequest;
import com.wannabe.app.main.data.dto.request.virtuality.VirtualCounselRequest;
import com.wannabe.app.main.data.dto.response.virtual.SuccessCreateVirtualResponse;
import com.wannabe.app.main.data.dto.virutality.response.MyCounselHospitalListResponse;
import com.wannabe.app.main.data.dto.virutality.response.MyVirtualSurgeryListResponse;
import com.wannabe.app.main.data.dto.virutality.response.MyVirtualSurgeryResponse;
import com.wannabe.app.main.data.dto.virutality.response.VirtualSurgeryDetailResponse;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.response.ListResponseDto;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.UserService;
import com.wannabe.app.main.service.VirtualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@RequestMapping(value = "/virtuality")
@Tag(name = "가상 성형")
public class VirtualityController {

    private final UserService userService;
    private final VirtualService virtualService;

    @PostMapping("")
    @Operation(summary = "가상 성형 이미지 등록")
    public Callable<Response<SuccessCreateVirtualResponse>> createVirtualSurgery(
        @RequestAttribute(USER_ID) long userId,
        @RequestPart(value = "request") CreateVirtualRequest request,
        @RequestPart(value = "beforeImage", required = false) MultipartFile beforeImage,
        @RequestPart(value = "afterImage", required = false) MultipartFile afterImage
    ) {
        return () -> Response.of(
            SuccessCreateVirtualResponse.of(virtualService.createVirtualSurgery(userId, request, beforeImage, afterImage))
        );
    }

    @PostMapping(value = "/android", consumes = {"multipart/form-data"})
    @Operation(summary = "가상 성형 이미지 등록 (안드로이드 전용)")
    public Callable<Response<SuccessCreateVirtualResponse>> createVirtualSurgeryAndroid(
        @RequestAttribute(USER_ID) long userId,
        @ModelAttribute("request") CreateVirtualAndroidRequest request
    ) {
        return () -> Response.of(
            SuccessCreateVirtualResponse.of(virtualService.createVirtualSurgeryByAndroid(userId, request))
        );
    }

    @GetMapping("test/category")
    @AnonymousCallable
    @Operation(summary = "가상 성형 카테고리 조회 테스트")
    public List<VirtualCategoryRequest> getVirtualCategory() {
        return virtualService.getVirtualCategory();
    }

    @PutMapping("/{virtualId}")
    @Operation(summary = "가상 성형 이미지 수정")
    public Callable<Response<Void>> updateVirtualSurgery(
        @RequestAttribute(USER_ID) long userId,
        @PathVariable long virtualId,
        @RequestPart(value = "request", required = false) CreateVirtualRequest request,
        @RequestPart(value = "afterImage") MultipartFile afterImage
    ) {
        virtualService.updateVirtualSurgery(userId, virtualId, request, afterImage);
        return Response::new;
    }

    @PutMapping(value = "android/{virtualId}", consumes = {"multipart/form-data"})
    @Operation(summary = "가상 성형 이미지 수정 (안드로이드 전용)")
    public Callable<Response<Void>> updateVirtualSurgeryByAndroid(
        @RequestAttribute(USER_ID) long userId,
        @PathVariable long virtualId,
        @ModelAttribute("request") CreateVirtualAndroidRequest request
    ) {
        virtualService.updateVirtualSurgeryAndroid(userId, virtualId, request);
        return Response::new;
    }

    @PostMapping(value = "/{virtualId}/side", consumes = {"multipart/form-data"})
    @Operation(summary = "가상 성형 45도 이미지 등록 (안드로이드 전용)")
    public Callable<Response<SuccessCreateVirtualResponse>> createVirtualSurgeryOnSide(
        @RequestAttribute(USER_ID) long userId,
        @PathVariable long virtualId,
        @RequestPart(value = "leftImage") MultipartFile leftImage,
        @RequestPart(value = "rightImage") MultipartFile rightImage
    ) {
        return () -> Response.of(
            SuccessCreateVirtualResponse.of(virtualService.createVirtualSurgeryOnSide(userId, virtualId, leftImage, rightImage))
        );
    }

    @PostMapping(value = "/android/{virtualId}/side", consumes = {"multipart/form-data"})
    @Operation(summary = "가상 성형 45도 이미지 등록 (안드로이드 전용)")
    public Callable<Response<SuccessCreateVirtualResponse>> createVirtualSurgeryOnSideAndroid(
        @RequestAttribute(USER_ID) long userId,
        @PathVariable long virtualId,
        @RequestParam(value = "leftImage") MultipartFile leftImage,
        @RequestParam(value = "rightImage") MultipartFile rightImage
    ) {
        return () -> Response.of(
            SuccessCreateVirtualResponse.of(virtualService.createVirtualSurgeryOnSide(userId, virtualId, leftImage, rightImage))
        );
    }

    @GetMapping("/{virtualId}/detail")
    @Operation(summary = "가상 성형 상세 조회")
    public Callable<Response<VirtualSurgeryDetailResponse>> getVirtualSurgeryDetail(
        @RequestAttribute(USER_ID) long userId,
        @PathVariable long virtualId
    ) {
        return () -> Response.of(virtualService.getVirtualSurgeryDetail(userId, virtualId));
    }

    @PostMapping(value = "/counsel")
    @Operation(summary = "가상 성형 상담 신청")
    public Callable<Response<Void>> createVirtualSurgeryCounsel(
        @RequestAttribute(USER_ID) long userId,
        @Valid @RequestBody VirtualCounselRequest request
    ) {
        virtualService.createVirtualSurgeryCounsel(getUser(userId), request);
        return Response::new;
    }

    private User getUser(long userId) {
        return userService.getUser(userId);
    }

    @GetMapping("")
    @Operation(summary = "나의 가상 성형 저장 / 상담 신청 목록 조회")
    public Callable<Response<ListResponseDto<MyVirtualSurgeryListResponse>>> getMyVirtualSurgeries(
        @RequestAttribute(USER_ID) Long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<MyVirtualSurgeryListResponse> myVirtualSurgeryList = virtualService.getMyVirtualSurgeries(userId, page, size)
                .map(MyVirtualSurgeryListResponse::from);
            return Response.of(myVirtualSurgeryList);
        };
    }

    @DeleteMapping("/{virtualId}")
    @Operation(summary = "저장된 가상 성형 삭제")
    public Callable<Response<Boolean>> deleteVirtualSurgery(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable Long virtualId
    ) {
        return () -> {
            virtualService.deleteVirtualSurgery(userId, virtualId);
            return Response.of(true);
        };
    }

    @GetMapping("/{virtualId}")
    @Operation(summary = "나의 가상 성형 단일 조회")
    public Callable<Response<MyVirtualSurgeryResponse>> getMyVirtualSurgery(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable Long virtualId
    ) {
        return () -> {
            MyVirtualSurgeryResponse myVirtualSurgery = MyVirtualSurgeryResponse.from(virtualService.getMyVirtualSurgery(userId, virtualId));
            return Response.of(myVirtualSurgery);
        };
    }

    @GetMapping("/{virtualId}/hospitals")
    @Operation(summary = "가상 성형 병원 목록 조회")
    public Callable<Response<ListResponseDto<MyCounselHospitalListResponse>>> getCounselHospitals(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable Long virtualId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<MyCounselHospitalListResponse> counselHospitalList = virtualService.getCounselHospitals(userId, virtualId, page, size)
                .map(MyCounselHospitalListResponse::from);
            return Response.of(counselHospitalList);
        };
    }

}
