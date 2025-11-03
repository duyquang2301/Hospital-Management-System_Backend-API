package com.wannabe.app.main.data.entity;

import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.community.request.CreateArticleRequest;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleBlock {

    private Long id;
    private Long articleId;
    private Long userId;
    private LocalDateTime createAt;

    public ArticleBlock(Long articleId, Long userId) {
        this.articleId = articleId;
        this.userId = userId;
    }
}
