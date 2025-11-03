package com.wannabe.app.main.data.dto.article;

import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.community.ArticleImage;
import com.wannabe.app.main.data.entity.Article;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetArticleDetailDto {

    private Long id;
    private YN isBookMark;
    private List<String> category;
    private Long writerId;
    private String profileImg;
    private String nickName;
    private LocalDateTime createAt;
    private Integer viewCount;
    private String content;
    private List<ArticleImage> image;
    private YN isAuthor;

    public static GetArticleDetailDto of(
        Article article,
        YN isBookMark,
        String profileImg,
        String nickName,
        List<ArticleImage> image,
        YN isAuthor
    ) {
        return new GetArticleDetailDto(
            article.getId(),
            isBookMark,
            article.getCategory(),
            article.getWriterId(),
            profileImg,
            nickName,
            article.getCreateAt(),
            article.getViewCount(),
            article.getContent(),
            image,
            isAuthor
        );
    }
}
