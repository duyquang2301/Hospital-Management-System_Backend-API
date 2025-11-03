package com.wannabe.app.main.controller;

import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.community.response.ReviewListResponse;
import com.wannabe.app.main.data.dto.banner.BannerResponse;
import com.wannabe.app.main.data.dto.home.response.PopularEventListResponse;
import com.wannabe.app.main.data.dto.home.response.PopularHospitalListResponse;
import com.wannabe.app.main.data.entity.Announcement;
import com.wannabe.app.main.response.ListResponseDto;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.AnnouncementService;
import com.wannabe.app.main.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.Callable;

@RestController
@RequestMapping(value = "/home")
@RequiredArgsConstructor
@Tag(name = "Home")
public class HomeController {

    private final HomeService homeService;
    private final AnnouncementService announcementService;

    @GetMapping(value = "/popularEvents")
    @Operation(summary = "믾이 본 이벤트")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<PopularEventListResponse>>> getPopularEvents(
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "20") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<PopularEventListResponse> eventList = homeService.getPopularEvents(page, size).map(PopularEventListResponse::from);
            return Response.of(eventList);
        };
    }

    @GetMapping(value = "/popularHospitals")
    @Operation(summary = "회원들에게 인기있는 병원")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<PopularHospitalListResponse>>> getPopularHospitals(
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "20") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<PopularHospitalListResponse> hospitalList = homeService.getPopularHospitals(page, size)
                .map(PopularHospitalListResponse::from);
            return Response.of(hospitalList);
        };
    }

    @GetMapping(value = "/popularReviews")
    @Operation(summary = "미리해본 시술 후기")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<ReviewListResponse>>> getReviews(
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "20") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<ReviewListResponse> reviewList = homeService.getReviews(page, size).map(ReviewListResponse::from);
            return Response.of(reviewList);
        };
    }

    @GetMapping(value = "/banners")
    @Operation(summary = "배너 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<BannerResponse>>> getBanners() {
        return () -> {
            ListResponseDto<BannerResponse> bannerList = homeService.getBanners().map(BannerResponse::from);
            return Response.of(bannerList);
        };
    }
    @GetMapping(value = "/mainBanner")
    @Operation(summary = "메인 배너 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<BannerResponse>>> getMainBanner() {
        return () -> {
            ListResponseDto<BannerResponse> mainBannerList = homeService.getMainBanners()
                .map(BannerResponse::from);
            return Response.of(mainBannerList);
        };
    }

    @GetMapping(value = "/annos")
    @Operation(summary = "공지사항 조회")
    @AnonymousCallable
    public Callable<Response<List<Announcement>>> getAnnos() {
        return () -> {

            List<Announcement> result = announcementService.getAnnouncementsActive();
            return Response.of(result);
        };
    }
}
