package com.wannabe.app.main.controller;

import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.event.PromotionBannerDTO;
import com.wannabe.app.main.data.dto.event.PromotionFilter;
import com.wannabe.app.main.data.dto.event.response.EventListResponse;
import com.wannabe.app.main.data.dto.event.response.EventResponse;
import com.wannabe.app.main.data.dto.request.event.EventCounselRequest;
import com.wannabe.app.main.data.dto.request.event.EventIdRequest;
import com.wannabe.app.main.data.dto.response.event.EventDetailResponse;
import com.wannabe.app.main.data.dto.response.event.PromotionElementResponse;
import com.wannabe.app.main.data.dto.response.event.PromotionListResponse;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.response.ListResponseDto;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.AuthService;
import com.wannabe.app.main.service.EventService;
import com.wannabe.app.main.utility.constant.HeaderKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/events")
@Tag(name = "Event")
public class EventController {

    private final AuthService authService;
    private final EventService eventService;

    @PostMapping("/{eventId}/counsel")
    @Operation(summary = "이벤트 상담 신청")
    public Callable<Response<Void>> createEventCounsel(
        @RequestBody EventCounselRequest eventCounselRequest,
        @PathVariable long eventId,
        HttpServletRequest httpServletRequest) {
        User user = getUser(httpServletRequest);
        eventService.createEventCounsel(user, eventId, eventCounselRequest);
        return Response::ok;
    }

    @GetMapping(value = "")
    @Operation(summary = "이벤트 목록 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<EventListResponse>>> getEvents(
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @Parameter(description = "조회할 지역의 2뎁스") @RequestParam(required = false) List<String> district,
        @Parameter(description = "조회할 지역의 1뎁스") @RequestParam(required = false) String city,
        @Parameter(description = "정렬할 컬럼") @RequestParam(defaultValue = "LATEST") String sort,
        @Parameter(description = "조회할 카테고리의 1뎁스") @RequestParam(required = false) String category,
        @Parameter(description = "조회할 카테고리의 2뎁스") @RequestParam(required = false) String categoryDetail
    ) {
        return () -> {
            PromotionFilter filter = PromotionFilter.of(size, page, sort, district, city, category, categoryDetail);
            ListResponseDto<EventListResponse> eventList = eventService.getEvents(filter).map(EventListResponse::from);
            return Response.of(eventList);
        };
    }

    @PostMapping("/{eventId}/bookmark")
    @Operation(summary = "이벤트 스크랩 등록")
    public Callable<Response<Boolean>> scrapEvent(@PathVariable long eventId, HttpServletRequest httpServletRequest) {
        User user = getUser(httpServletRequest);
        eventService.scrapEvent(user, eventId);
        return () -> Response.of(true);
    }

    @DeleteMapping(value = "/{eventId}/bookmark")
    @Operation(summary = "이벤트 스크랩 해제")
    public Callable<Response<Boolean>> deleteEvent(@PathVariable long eventId, HttpServletRequest httpServletRequest) {
        User user = getUser(httpServletRequest);
        eventService.deleteEvent(user.getId(), eventId);
        return () -> Response.of(true);
    }

    @PostMapping("/{eventId}")
    @Operation(summary = "이벤트 상세 조회")
    public Callable<Response<EventDetailResponse>> getEventWithUser(
        @PathVariable long eventId,
        @RequestBody(required = false) EventIdRequest beforeEventRequest,
        HttpServletRequest request) {
            User user = getUser(request);
            return () -> Response.of(eventService.getEventDetail(eventId, user, beforeEventRequest));
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "이벤트 상세 조회")
    @AnonymousCallable
    public Callable<Response<EventDetailResponse>> getEvent(
        @PathVariable long eventId) {
        return  () -> Response.of(eventService.getEventDetailSingle(eventId));
    }

    @GetMapping("/{eventId}/recommend")
    @Operation(summary = "많이 본 추천 이벤트 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<EventListResponse>>> getRecommendEvents(
        @PathVariable long eventId
    ) {
        return () -> {
            ListResponseDto<EventListResponse> eventList = eventService.getRecommendEvents(eventId).map(EventListResponse::from);
            return Response.of(eventList);
        };
    }

    @GetMapping("/promotion")
    @Operation(summary = "기획전 목록 조회")
    @AnonymousCallable
    public Callable<Response<PromotionListResponse>> getPromotions(
        @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @RequestParam(value = "cursor", required = false) Long cursor
    ) {
        return () -> Response.of(eventService.getPromotionList(size, cursor));
    }

    @GetMapping("/promotion/{promotionId}")
    @Operation(summary = "기획전 상세 조회")
    public Callable<Response<PromotionElementResponse>> getPromotion(
        @PathVariable long promotionId,
        @RequestParam(defaultValue = "20") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @RequestParam(required = false) List<String> district,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String categoryDetail,
        @RequestParam(defaultValue = "LATEST") String sort
    ) {
        PromotionFilter filter = PromotionFilter.of(promotionId, size, page, sort, district, city, category, categoryDetail);
        return () -> Response.of(eventService.getPromotion(promotionId, filter));
    }

    @GetMapping("/promotion/banner")
    @Operation(summary = "기획전 배너 조회")
    @AnonymousCallable
    public Callable<Response<PromotionBannerDTO>> getPromotionBanner() {
        return () -> Response.of(eventService.getPromotionBanner());
    }

    @GetMapping("/list/review")
    @Operation(summary = "모든 이벤트 목록")
    public Callable<Response<ListResponseDto<EventResponse>>> getAllEventList(
        @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page
    ) {
        return () -> {
            ListResponseDto<EventResponse> eventLists = eventService.getAllEventList(page, size).map(EventResponse::from);
            return Response.of(eventLists);
        };
    }

    private User getUser(HttpServletRequest request) {
        try {
            return authService.getUser(request);
        } catch (Exception e) {
            return null;
        }
    }
}
