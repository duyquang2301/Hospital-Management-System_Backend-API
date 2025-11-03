package com.wannabe.app.main.data.dto.response.review;

import com.wannabe.app.main.data.dto.article.GetReviewListDto;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.state.ReviewType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventReviewListResponse {

    @Schema(description = "게시글 아이디")
    Long id;

    @Schema(description = "작성자 아이디")
    Long writerId;

    @Schema(description = "작성자 프로필 이미지")
    String profileImg;

    @Schema(description = "작성자 닉네임")
    String nickName;

    @Schema(description = "후기 작성일")
    LocalDateTime createAt;

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

    public static EventReviewListResponse from(GetReviewListDto review) {
        return new EventReviewListResponse(
            review.getId(),
            review.getWriterId(),
            review.getProfileImg(),
            review.getNickName(),
            review.getCreateAt(),
            review.getViewCount(),
            review.getCommentCount(),
            review.getImages(),
            review.getContent(),
            review.getIsAuthor(),
            review.getReviewType()
        );
    }
}
