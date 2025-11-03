package com.wannabe.app.main.data.dto.community.response;

import com.wannabe.app.main.data.dto.article.GetArticleDetailDto;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.community.ArticleImage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleDetailResponse {

    @Schema(description = "게시글 아이디")
    private final Long id;

    @Schema(description = "북마크 여부")
    private final YN isBookmark;

    @Schema(description = "카테고리")
    private final List<String> category;

    @Schema(description = "작성자 아이디")
    private final Long writerId;

    @Schema(description = "게시글 작성자 프로필 이미지")
    private final String profileImg;

    @Schema(description = "게시글 작성자 닉네임")
    private final String nickname;

    @Schema(description = "게시글 작성일")
    private final LocalDateTime createAt;

    @Schema(description = "조회수")
    private final Integer viewCount;

    @Schema(description = "게시글 내용")
    private final String content;

    @Schema(description = "게시글 이미지")
    private final List<ArticleImage> image;

    @Schema(description = "작성자 여부")
    private final YN isAuthor;

    public static ArticleDetailResponse from(GetArticleDetailDto getArticleDetail) {
        return new ArticleDetailResponse(
            getArticleDetail.getId(),
            getArticleDetail.getIsBookMark(),
            getArticleDetail.getCategory(),
            getArticleDetail.getWriterId(),
            getArticleDetail.getProfileImg(),
            getArticleDetail.getNickName(),
            getArticleDetail.getCreateAt(),
            getArticleDetail.getViewCount(),
            getArticleDetail.getContent(),
            getArticleDetail.getImage(),
            getArticleDetail.getIsAuthor()
        );
    }
}
