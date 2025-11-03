package com.wannabe.app.main.data.dto.comment.request;

import com.wannabe.app.main.data.dto.common.YN;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {

    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;

    private Long commentId;

    private YN isReply = YN.N;

}
