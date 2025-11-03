package com.wannabe.app.main.controller;

import static com.wannabe.app.main.utility.constant.HeaderKey.USER_ID;

import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.data.dto.comment.request.CommentRequest;
import com.wannabe.app.main.data.dto.comment.request.UpdateCommentRequest;
import com.wannabe.app.main.data.dto.comment.response.CommentListResponse;
import com.wannabe.app.main.response.ListResponseDto;
import com.wannabe.app.main.response.Response;
import com.wannabe.app.main.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Comment")
public class CommentController {

    private final CommentService commentService;

    @GetMapping(value = "/{articleId}/comment")
    @Operation(summary = "댓글 목록")
    @AnonymousCallable
    public Callable<Response<ListResponseDto<CommentListResponse>>> getComments(
        @PathVariable Long articleId,
        @RequestAttribute(name = USER_ID, required = false) Long userId,
        @Parameter(description = "page (현재 페이지)") @RequestParam(defaultValue = "0") @Min(value = 0, message = "page 는 0 이하일 수 없습니다.") int page,
        @Parameter(description = "size (가져올 개수)") @RequestParam(defaultValue = "10") @Min(value = 0, message = "size 는 0 이하일 수 없습니다.") int size
    ) {
        return () -> {
            ListResponseDto<CommentListResponse> comment = commentService.getComments(articleId, page, size, userId).map(CommentListResponse::from);
            return Response.of(comment);
        };
    }

    @PostMapping(value = "/{articleId}")
    @Operation(summary = "댓글 작성")
    public Callable<Response<Boolean>> createComment(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable long articleId,
        @Valid @RequestBody CommentRequest commentRequest
    ) {
        return () -> {
            commentService.createComment(articleId, userId, commentRequest);
            return Response.of(true);
        };
    }

    @PutMapping(value = "/{articleId}")
    @Operation(summary = "댓글/답글 수정")
    public Callable<Response<Boolean>> updateComment(
        @RequestAttribute(USER_ID) Long userId,
        @PathVariable long articleId,
        @Valid @RequestBody UpdateCommentRequest updateCommentRequest
    ) {
        return () -> {
            commentService.updateComment(userId, articleId, updateCommentRequest);
            return Response.of(true);
        };
    }

    @DeleteMapping(value = "/{commentId}")
    @Operation(summary = "댓글/답글 삭제")
    public Callable<Response<Boolean>> deleteComment(@RequestAttribute(USER_ID) Long userId, @PathVariable long commentId) {
        return () -> {
            commentService.deleteComment(userId, commentId);
            return Response.of(true);
        };
    }
}
