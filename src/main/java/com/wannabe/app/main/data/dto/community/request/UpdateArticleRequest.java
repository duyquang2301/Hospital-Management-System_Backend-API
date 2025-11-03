package com.wannabe.app.main.data.dto.community.request;

import com.wannabe.app.main.data.dto.community.ArticleImage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateArticleRequest {

    @Schema(description = "후기 아이디")
    Long reviewId;

    @Schema(description = "게시글 내용")
    @NotBlank(message = "게시글 내용은 필수 입력 값입니다.")
    String content;

    @Schema(description = "삭제한 사진 아이디")
    List<ArticleImage> deletedImages;

}
