package com.wannabe.app.main.controller;

import static com.wannabe.app.main.utility.constant.HeaderKey.USER_ID;

import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.common.Filter;
import com.wannabe.app.main.data.dto.community.response.ArticleListResponse;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetHospitalDetailDto;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetHospitalDto;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetHospitalEventsDto;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetSearchHospitalsDto;
import com.wannabe.app.main.data.dto.hospital.response.GetHospitalResponse;
import com.wannabe.app.main.data.dto.request.hospital.CounselRequest;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.response.ListResponseDto;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.AuthService;
import com.wannabe.app.main.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/hospital")
@RequiredArgsConstructor
@Tag(name = "Hospital")
public class HospitalController {

    private final HospitalService hospitalService;
    private final AuthService authService;

    @GetMapping(value = "/recommend")
    @Operation(summary = "추천 병원 목록 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<GetHospitalResponse>>> getRecommendHospitals(
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @Parameter(description = "조회할 지역의 1뎁스") @RequestParam(required = false) String city,
        @Parameter(description = "조회할 지역의 2뎁스") @RequestParam(required = false) List<String> district,
        @Parameter(description = "정렬할 컬럼", example = "CONSULT_COUNT, LATEST") @RequestParam(defaultValue = "OLDEST") String sort,
        @Parameter(description = "검색 카테고리") @RequestParam(required = false) List<String> category
    ) {
        return () -> {
            Filter filter = Filter.of(size, page, sort, city, district, category, "");
            ListResponseDto<GetHospitalResponse> recommendHospitalList = hospitalService.getRecommendHospitals(filter)
                .map(GetHospitalResponse::from);
            return Response.of(recommendHospitalList);
        };
    }

    @GetMapping(value = "/search")
    @Operation(summary = "검색 병원 목록 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<GetHospitalResponse>>> getHospitals(
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @Parameter(description = "조회할 지역의 1뎁스") @RequestParam(required = false) String city,
        @Parameter(description = "조회할 지역의 2뎁스") @RequestParam(required = false) List<String> district,
        @Parameter(description = "정렬할 컬럼", example = "CONSULT_COUNT, LATEST") @RequestParam(defaultValue = "OLDEST") String sort,
        @Parameter(description = "검색 카테고리") @RequestParam(required = false) List<String> category,
        @Parameter(description = "검색어") @RequestParam(required = false) String keyword
    ) {
        return () -> {
            Filter filter = Filter.of(size, page, sort, city, district, category, keyword);
            ListResponseDto<GetHospitalResponse> searchHospitalList = hospitalService.getHospitals(filter)
                .map(GetHospitalResponse::from);
            return Response.of(searchHospitalList);
        };
    }

    @GetMapping(value = "/{hospitalId}")
    @Operation(summary = "병원 상세 내역 조회")
    @AnonymousCallable
    public Callable<Response<GetHospitalDto>> getHospital(
        @PathVariable long hospitalId,
        @RequestAttribute(name = USER_ID, required = false) Long userId
        ) {
        if (userId == null) {
            return () -> Response.of(hospitalService.getHospitalSingle(hospitalId));
        }
        return () -> Response.of(hospitalService.getHospital(hospitalId, userId));
    }

    @GetMapping(value = "/{hospitalId}/detail")
    @Operation(summary = "병원 정보 조회")
    @AnonymousCallable
    public Callable<Response<GetHospitalDetailDto>> getHospitalDetail(@PathVariable long hospitalId) {
        return () -> Response.of(hospitalService.getHospitalDetail(hospitalId));
    }

    @GetMapping(value = "/{hospitalId}/events")
    @Operation(summary = "병원 이벤트 목록 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<GetHospitalEventsDto>>> getHospitalEvents(
        @PathVariable long hospitalId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @Parameter(description = "정렬할 컬럼", example = "date_created, consult_count, price, low_price") @RequestParam(defaultValue = "date_created") String sort,
        @Parameter(description = "검색 카테고리") @RequestParam(required = false) List<String> category
    ) {
        return () -> Response.of(hospitalService.getHospitalEvents(hospitalId, page, size, sort, category));
    }

    @GetMapping(value = "/{hospitalId}/reviews")
    @Operation(summary = "병원 후기 목록 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<ArticleListResponse>>> getHospitalReviews(
        @PathVariable long hospitalId,
        @RequestAttribute(name = USER_ID, required = false) Long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @Parameter(description = "정렬할 컬럼", example = "date_created, view_count") @RequestParam(defaultValue = "date_created") String sort,
        @Parameter(description = "검색 카테고리") @RequestParam(required = false) List<String> category
    ) {
        return () -> {
            ListResponseDto<ArticleListResponse> reviewList = hospitalService.getHospitalReviews(hospitalId, page, size, sort, category, userId)
                .map(ArticleListResponse::from);
            return Response.of(reviewList);
        };
    }

    @PostMapping(value = "/{hospitalId}/bookmark")
    @Operation(summary = "병원 북마크 생성")
    public Callable<Response<Boolean>> createHospitalBookMark(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable long hospitalId
    ) {
        return () -> {
            hospitalService.createHospitalBookMark(hospitalId, userId);
            return Response.of(true);
        };
    }

    @DeleteMapping(value = "/{hospitalId}/bookmark")
    @Operation(summary = "병원 북마크 해제")
    public Callable<Response<Boolean>> deleteHospitalBookMark(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable long hospitalId
    ) {
        return () -> {
            hospitalService.deleteHospitalBookMark(hospitalId, userId);
            return Response.of(true);
        };
    }

    @PostMapping(value = "/{hospitalId}/counsel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "병원 상담 신청")
    public Callable<Response<Void>> createCounsel(@PathVariable long hospitalId,
        @RequestPart(value = "counselRequest") @Valid CounselRequest counselRequest,
        @RequestPart(value = "galleryImage", required = false) List<MultipartFile> galleryImage,
        HttpServletRequest request) {
        hospitalService.createCounsel(getUser(request), hospitalId, counselRequest, galleryImage);
        return Response::ok;
    }

    @GetMapping(value = "/{hospitalId}/counsel")
    @Operation(summary = "병원 상담 신청 목록")
    public Callable<Response<GetSearchHospitalsDto>> getHospitalCounsel(@PathVariable long hospitalId) {
        return () -> new Response<>(hospitalService.getSearchHospitalsDto(hospitalId));
    }

    private User getUser(HttpServletRequest request) {
        return authService.getUser(request);
    }
}
