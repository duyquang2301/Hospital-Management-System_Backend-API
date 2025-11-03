package com.wannabe.app.main.data.dto.community.response;

import com.wannabe.app.main.data.dto.article.GetReviewListDto;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.state.ReviewType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewListResponse {

    @Schema(description = "게시글 아이디")
    Long id;

    @Schema(description = "작성자 아이디")
    Long writerId;

    @Schema(description = "작성자 프로필 이미지")
    String profileImg;

    @Schema(description = "작성자 닉네임")
    String nickName;

    @Schema(description = "성형 전 이미지")
    String beforeImage;

    @Schema(description = "성형 후 이미지")
    String afterImage;

    @Schema(description = "후기 작성일")
    LocalDateTime createAt;
//    @Schema(description = "가격")
//    Integer cost;

    @Schema(description = "조회 수")
    private Integer viewCount;

    @Schema(description = "댓글 수")
    private Integer commentCount;

    @Schema(description = "이미지")
    List<String> images;

    @Schema(description = "게시글 내용")
    String content;

    @Schema(description = "게시글 작성 여부")
    YN isAuthor;

    @Schema(description = "후기 타입")
    ReviewType reviewType;

    public static ReviewListResponse from(GetReviewListDto reviewList) {
        return new ReviewListResponse(
            reviewList.getId(),
            reviewList.getWriterId(),
            reviewList.getProfileImg(),
            reviewList.getNickName(),
            reviewList.getBeforeImage(),
            reviewList.getAfterImage(),
            reviewList.getCreateAt(),
            reviewList.getViewCount(),
            reviewList.getCommentCount(),
            reviewList.getImages(),
            reviewList.getContent(),
            reviewList.getIsAuthor(),
            reviewList.getReviewType()
        );
    }
}
