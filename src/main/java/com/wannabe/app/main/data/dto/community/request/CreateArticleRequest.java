package com.wannabe.app.main.data.dto.community.request;

import com.wannabe.app.main.annotation.ArticleTypeValid;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.ReviewType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateArticleRequest {

    @Schema(description = "게시글 타입")
    @ArticleTypeValid(enumClass = ArticleType.class, message = "게시글 타입은 필수 값입니다.", ignoreCase = true)
    ArticleType articleType;

    @Schema(description = "후기 타입")
    ReviewType reviewType;

    @Schema(description = "후기 아이디")
    Long reviewId;

    @Schema(description = "작성자가 선택한 카테고리")
    @NotEmpty(message = "카테고리는 필수 선택 값입니다.")
    List<String> category;

    @Schema(description = "게시글 내용")
    @NotBlank(message = "게시글 내용은 필수 입력 값입니다.")
    String content;

}
