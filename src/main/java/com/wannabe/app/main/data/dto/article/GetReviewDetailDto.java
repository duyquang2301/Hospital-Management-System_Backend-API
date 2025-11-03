package com.wannabe.app.main.data.dto.article;

import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.community.ArticleImage;
import com.wannabe.app.main.data.dto.event.GetEventDto;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.ReviewType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetReviewDetailDto {

    private Long id;
    private YN isBookMark;
    private ArticleType type;
    private ReviewType reviewType;
    private List<String> category;
    private Long writerId;
    private String profileImg;
    private String nickName;
    private LocalDateTime createAt;
    private Integer viewCount;
    private String content;
    private List<ArticleImage> image;
    private String beforeImage;
    private String afterImage;
    private GetEventDto event;
    private YN isAuthor;

    public static GetReviewDetailDto of(
        CommonArticle common,
        List<ArticleImage> images,
        String beforeImage,
        String afterImage,
        GetEventDto event,
        YN isAuthor
    ) {
        Article article = common.article();
        return new GetReviewDetailDto(
            article.getId(),
            common.isBookMark(),
            article.getType(),
            article.getReviewType(),
            article.getCategory(),
            article.getWriterId(),
            common.profileImg(),
            common.user().getNickname(),
            article.getCreateAt(),
            article.getViewCount(),
            article.getContent(),
            images,
            beforeImage,
            afterImage,
            event,
            isAuthor
        );
    }

}
