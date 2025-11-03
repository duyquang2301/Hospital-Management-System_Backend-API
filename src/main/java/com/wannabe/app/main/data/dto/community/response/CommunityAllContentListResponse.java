package com.wannabe.app.main.data.dto.community.response;

import com.wannabe.app.main.data.dto.article.ArticleListDto;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.ReviewType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityAllContentListResponse {

    @Schema(description = "게시글 아이디")
    private Long id;

    @Schema(description = "게시글 작성자 아이디")
    private Long writerId;

    @Schema(description = "프로필 이미지")
    private String profileImg;

    @Schema(description = "닉네임")
    private String nickName;

    @Schema(description = "게시글 생성일")
    private LocalDateTime createAt;

    @Schema(description = "조회 수")
    private Integer viewCount;

    @Schema(description = "댓글 수")
    private Integer commentCount;

    @Schema(description = "글 내용")
    private String content;

    @Schema(description = "게시글 이미지")
    private List<String> images;

    @Schema(description = "게시글 후기 성형 전 이미지")
    private String beforeImage;

    @Schema(description = "게시글 후기 성형 후 이미지")
    private String afterImage;

//    @Schema(description = "가격")
//    private Integer cost;

    @Schema(description = "게시글 타입")
    private ArticleType type;

    @Schema(description = "후기 타입")
    private ReviewType reviewType;

    @Schema(description = "게시글 수정 여부")
    private YN isAuthor;

    public static CommunityAllContentListResponse from(ArticleListDto articleDto) {
        return new CommunityAllContentListResponse(
            articleDto.getId(),
            articleDto.getWriterId(),
            articleDto.getProfileImg(),
            articleDto.getNickName(),
            articleDto.getCreateAt(),
            articleDto.getViewCount(),
            articleDto.getCommentCount(),
            articleDto.getContent(),
            articleDto.getImages(),
            articleDto.getBeforeImage(),
            articleDto.getAfterImage(),
//            articleDto.getCost(),
            articleDto.getType(),
            articleDto.getReviewType(),
            articleDto.getIsAuthor()
        );
    }
}
