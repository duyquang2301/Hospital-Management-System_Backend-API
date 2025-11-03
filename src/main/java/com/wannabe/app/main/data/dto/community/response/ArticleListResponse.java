package com.wannabe.app.main.data.dto.community.response;

import com.wannabe.app.main.data.dto.article.GetArticleListDto;
import com.wannabe.app.main.data.dto.common.YN;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleListResponse {

    @Schema(description = "게시글 아이디")
    private final Long id;

    @Schema(description = "작성자 아이디")
    private final Long writerId;

    @Schema(description = "작성자 프로필 이미지")
    private final String profileImg;

    @Schema(description = "작성자 닉네임")
    private final String nickName;

    @Schema(description = "작성일")
    private final LocalDateTime createAt;

    @Schema(description = "조회수")
    private final Integer viewCount;

    @Schema(description = "댓글수")
    private final Integer commentCount;

    @Schema(description = "이미지")
    private final List<String> images;

    @Schema(description = "내용")
    private final String content;

    @Schema(description = "게시글 작성자 여부")
    private final YN isAuthor;

    public static ArticleListResponse from(GetArticleListDto getArticleListDto) {
        return new ArticleListResponse(
            getArticleListDto.getId(),
            getArticleListDto.getWriterId(),
            getArticleListDto.getProfileImg(),
            getArticleListDto.getNickName(),
            getArticleListDto.getCreateAt(),
            getArticleListDto.getViewCount(),
            getArticleListDto.getCommentCount(),
            getArticleListDto.getImage(),
            getArticleListDto.getContent(),
            getArticleListDto.getIsAuthor()
        );
    }
}
