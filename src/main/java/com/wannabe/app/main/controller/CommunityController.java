package com.wannabe.app.main.controller;

import static com.wannabe.app.main.utility.constant.HeaderKey.USER_ID;

import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.community.ArticleImage;
import com.wannabe.app.main.data.dto.community.request.CreateArticleRequest;
import com.wannabe.app.main.data.dto.community.request.UpdateArticleRequest;
import com.wannabe.app.main.data.dto.community.response.ArticleDetailResponse;
import com.wannabe.app.main.data.dto.community.response.ArticleListResponse;
import com.wannabe.app.main.data.dto.community.response.CommunityAllContentListResponse;
import com.wannabe.app.main.data.dto.community.response.ReviewDetailResponse;
import com.wannabe.app.main.data.dto.community.response.ReviewListResponse;
import com.wannabe.app.main.data.dto.response.review.EventReviewListResponse;
import com.wannabe.app.main.response.ListResponseDto;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "Community")
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "게시글 작성")
    public Callable<Response<Boolean>> createArticle(
        @RequestAttribute(USER_ID) Long userId,
        @RequestPart(value = "createArticleRequest") @Valid CreateArticleRequest createArticleRequest,
        @RequestPart(required = false, value = "images") List<MultipartFile> images
    ) {
        return () -> {
            communityService.createArticle(userId, createArticleRequest, images);
            return Response.of(true);
        };
    }

    @PutMapping(value = "/{articleId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "게시글 수정")
    public Callable<Response<Boolean>> updateArticle(
        @RequestAttribute(USER_ID) Long userId,
        @RequestPart(value = "updateArticleRequest") @Valid UpdateArticleRequest updateArticleRequest,
        @RequestPart(required = false, value = "images") List<MultipartFile> images,
        @RequestPart(required = false, value = "existImage") List<ArticleImage> existImage,
        @PathVariable Long articleId
    ) {
        return () -> {
            communityService.updateArticle(userId, updateArticleRequest, images, existImage, articleId);
            return Response.of(true);
        };
    }

    @GetMapping(value = "/review/event/{eventId}")
    @Operation(summary = "이벤트 시술 후기 목록 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<EventReviewListResponse>>> getEventReviews(
        @RequestAttribute(name = USER_ID, required = false) Long userId,
        @PathVariable long eventId
    ) {
        return () -> {
            ListResponseDto<EventReviewListResponse> eventReviewList = communityService.getEventReviews(eventId, userId)
                .map(EventReviewListResponse::from);
            return Response.of(eventReviewList);
        };
    }

    @GetMapping("")
    @Operation(summary = "커뮤니티 전체 글 목록 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<CommunityAllContentListResponse>>> getAllContents(
        @RequestAttribute(name = USER_ID, required = false) Long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @Parameter(description = "선택한 카테고리") @RequestParam(required = false) String category
    ) {
        return () -> {
            ListResponseDto<CommunityAllContentListResponse> allContent = communityService.getAllContent(page, size, category, userId)
                .map(CommunityAllContentListResponse::from);
            return Response.of(allContent);
        };
    }

    @GetMapping(value = "/reviews")
    @Operation(summary = "커뮤니티 시술 후기 목록 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<ReviewListResponse>>> getReviews(
        @RequestAttribute(name = USER_ID, required = false) Long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @Parameter(description = "선택한 카테고리") @RequestParam(required = false) String category
    ) {
        return () -> {
            ListResponseDto<ReviewListResponse> reviewList = communityService.getReviews(page, size, category, userId).map(ReviewListResponse::from);
            return Response.of(reviewList);
        };
    }

    @GetMapping(value = "/articles")
    @Operation(summary = "커뮤니티 일반 게시글 목록 조회")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<ArticleListResponse>>> getArticles(
        @RequestAttribute(name = USER_ID, required = false) Long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size,
        @Parameter(description = "선택한 카테고리") @RequestParam(required = false) String category
    ) {
        return () -> {
            ListResponseDto<ArticleListResponse> articleList = communityService.getArticles(page, size, category, userId)
                .map(ArticleListResponse::from);
            return Response.of(articleList);
        };
    }

    @GetMapping(value = "/article/{articleId}")
    @Operation(summary = "일반 게시글 상세")
    @AnonymousCallable
    public Callable<Response<ArticleDetailResponse>> getArticle(
        @RequestAttribute(name = USER_ID, required = false) Long userId,
        @PathVariable Long articleId
    ) {
        return () -> {
            ArticleDetailResponse article = ArticleDetailResponse.from(communityService.getArticle(userId, articleId));
            return Response.of(article);
        };
    }

    @GetMapping(value = "/review/{articleId}")
    @Operation(summary = "시술 후기 게시글 상세")
    @AnonymousCallable
    public Callable<Response<ReviewDetailResponse>> getReview(
        @RequestAttribute(name = USER_ID, required = false) Long userId,
        @PathVariable Long articleId
    ) {
        return () -> {
            ReviewDetailResponse review = ReviewDetailResponse.from(communityService.getReview(userId, articleId));
            return Response.of(review);
        };
    }

    @PostMapping(value = "/{articleId}/bookmark")
    @Operation(summary = "게시글(게시글, 리뷰..등) 북마크 생성")
    public Callable<Response<Boolean>> createArticleBookMark(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable Long articleId
    ) {
        return () -> {
            communityService.createArticleBookMark(articleId, userId);
            return Response.of(true);
        };
    }

    @DeleteMapping(value = "/{articleId}/bookmark")
    @Operation(summary = "게시글(게시글, 리뷰..등) 북마크 해제")
    public Callable<Response<Boolean>> deleteArticleBookMark(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable Long articleId
    ) {
        return () -> {
            communityService.deleteArticleBookMark(articleId, userId);
            return Response.of(true);
        };
    }

    @DeleteMapping(value = "/{articleId}")
    @Operation(summary = "게시글 삭제")
    public Callable<Response<Boolean>> deleteArticle(@PathVariable Long articleId) {
        return () -> {
            communityService.deleteArticle(articleId);
            return Response.of(true);
        };
    }

    @PostMapping(value = "/block/{articleId}")
    @Operation(summary = "게시글 신고")
    public Callable<Response<Boolean>> createArticleBlock(@RequestAttribute(USER_ID) Long userId,@PathVariable Long articleId) {
        return () -> {
            communityService.createArticleBlock(userId, articleId);
            return Response.of(true);
        };
    }

}
