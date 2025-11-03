package com.wannabe.app.main.data.dto.comment.response;

import com.wannabe.app.main.data.dto.comment.CommentDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentListResponse {

    @Schema(description = "댓글")
    private final CommentDto comments;

    public static CommentListResponse from(CommentDto commentDto) {
        return new CommentListResponse(commentDto);
    }

}
