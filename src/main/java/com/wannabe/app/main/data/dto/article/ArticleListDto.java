package com.wannabe.app.main.data.dto.article;

import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.ReviewType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleListDto {

    private Long id;
    private Long writerId;
    private String profileImg;
    private String nickName;
    private LocalDateTime createAt;
    private Integer viewCount;
    private Integer commentCount;
    private String content;
    private List<String> images;
    private String beforeImage;
    private String afterImage;
    //    private Integer cost;
    private ArticleType type;
    private ReviewType reviewType;
    private YN isAuthor;

    public static ArticleListDto of(
        Article article,
        String profileImg,
        String nickName,
        List<String> image,
        String beforeImage,
        String afterImage,
        YN isAuthor
//        Integer cost
    ) {
        return new ArticleListDto(
            article.getId(),
            article.getWriterId(),
            profileImg,
            nickName,
            article.getCreateAt(),
            article.getViewCount(),
            article.getCommentCount(),
            article.getContent(),
            image,
            beforeImage,
            afterImage,
//            cost,
            article.getType(),
            article.getReviewType(),
            isAuthor
        );
    }

}
