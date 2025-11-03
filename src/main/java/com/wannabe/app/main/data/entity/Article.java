package com.wannabe.app.main.data.entity;

import com.wannabe.app.main.data.dto.community.request.CreateArticleRequest;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.ReviewType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    private Long id;
    private Long writerId;
    private ArticleType type;
    private List<String> category;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Integer viewCount;
    private Integer commentCount;
    private Long imageGroupId;
    private Long beforeImageGroupId;
    private Long afterImageGroupId;
    private ReviewType reviewType;
    private Long reviewTypeId;

    // TODO 수정 필요
    public Article(Long writerId, CreateArticleRequest createArticleRequest) {
        this.writerId = writerId;
        this.type = createArticleRequest.getArticleType();
        this.reviewType = createArticleRequest.getReviewType();
        this.reviewTypeId = createArticleRequest.getReviewId();
        this.category = createArticleRequest.getCategory();
        this.content = createArticleRequest.getContent();
    }

    public void setContent(String content) {
        this.content = content;
    }
}
