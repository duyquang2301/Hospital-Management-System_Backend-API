package com.wannabe.app.main.data.dto.community.response;


import com.wannabe.app.main.data.dto.article.GetReviewDetailDto;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.community.ArticleImage;
import com.wannabe.app.main.data.dto.event.GetEventDto;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.ReviewType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ReviewDetailResponse {

    @Schema(description = "게시글 아이디")
    private final Long id;

    @Schema(description = "북마크 여부")
    private final YN isBookMark;

    @Schema(description = "게시글 타입")
    private final ArticleType type;

    @Schema(description = "후기 카테고리")
    private final ReviewType reviewType;

    @Schema(description = "카테고리")
    private final List<String> category;

    @Schema(description = "작성자 아이디")
    private final Long writerId;

    @Schema(description = "게시글 작성자 프로필 이미지")
    private final String profileImg;

    @Schema(description = "게시글 작성자 닉네임")
    private final String nickName;

    @Schema(description = "작성일")
    private final LocalDateTime createAt;

    @Schema(description = "조회수")
    private final Integer viewCount;

    @Schema(description = "게시글 내용")
    private final String content;

    @Schema(description = "게시글 이미지")
    private final List<ArticleImage> image;

    @Schema(description = "성형 전 이미지")
    private final String beforeImage;

    @Schema(description = "성형 후 이미지")
    private final String afterImage;

    @Schema(description = "시술 이벤트")
    private final GetEventDto event;

    @Schema(description = "작성자 여부")
    private final YN isAuthor;

    public static ReviewDetailResponse from(GetReviewDetailDto getReviewDetailDto) {
        return new ReviewDetailResponse(
            getReviewDetailDto.getId(),
            getReviewDetailDto.getIsBookMark(),
            getReviewDetailDto.getType(),
            getReviewDetailDto.getReviewType(),
            getReviewDetailDto.getCategory(),
            getReviewDetailDto.getWriterId(),
            getReviewDetailDto.getProfileImg(),
            getReviewDetailDto.getNickName(),
            getReviewDetailDto.getCreateAt(),
            getReviewDetailDto.getViewCount(),
            getReviewDetailDto.getContent(),
            getReviewDetailDto.getImage(),
            getReviewDetailDto.getBeforeImage(),
            getReviewDetailDto.getAfterImage(),
            getReviewDetailDto.getEvent(),
            getReviewDetailDto.getIsAuthor()
        );
    }


}
