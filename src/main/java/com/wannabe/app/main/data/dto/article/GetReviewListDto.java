package com.wannabe.app.main.data.dto.article;

import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.state.ReviewType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetReviewListDto {

    private Long id;
    private Long writerId;
    private String profileImg;
    private String nickName;
    private String beforeImage;
    private String afterImage;
    private LocalDateTime createAt;
    private Integer viewCount;
    private Integer commentCount;
    //    private Integer cost;
    private List<String> images;
    private String content;
    private ReviewType reviewType;
    private YN isAuthor;

    public static GetReviewListDto of(
        Article article,
        String profileImg,
        String nickName,
        String beforeImage,
        String afterImage,
        List<String> images,
        YN isAuthor
    ) {
        return new GetReviewListDto(
            article.getId(),
            article.getWriterId(),
            profileImg,
            nickName,
            beforeImage,
            afterImage,
//            cost,
            article.getCreateAt(),
            article.getViewCount(),
            article.getCommentCount(),
            images,
            article.getContent(),
            article.getReviewType(),
            isAuthor
        );
    }

}
