package com.wannabe.app.main.data.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCommentRequest {

    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;
    @NotNull(message = "댓글 아이디가 존재하지 않습니다.")
    private Long commentId;

}
