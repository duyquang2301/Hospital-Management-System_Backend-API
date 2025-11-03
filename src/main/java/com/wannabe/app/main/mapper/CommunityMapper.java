package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.entity.ArticleBlock;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CommunityMapper {

    // TODO 쿼리 수정 필요
    List<Article> findAllArticle(
        @Param("page") int page,
        @Param("size") int size,
        @Param("category") String category,
        @Param("userId") Long userId
    );

    long countAll(@Param("category") String category);

    List<Article> findAllReviews(
        @Param("page") int page,
        @Param("size") int size,
        @Param("category") String category,
        @Param("userId") Long userId
    );

    long countAllReview(@Param("category") String category);

    List<Article> findReviewListByEventId(@Param("eventId") long eventId);

    long countAllReviewByEventId(@Param("eventId") long eventId);

    List<Article> findAllNormalArticle(
        @Param("page") int page,
        @Param("size") int size,
        @Param("category") String category,
        @Param("userId") Long userId
    );

    long countAllNormalArticle(@Param("category") String category);

    Optional<Article> findByArticleId(@Param("articleId") Long articleId);

    void updateViewCount(@Param("articleId") Long articleId);

    void updateCommentCount(@Param("articleId") Long articleId);

    void saveArticle(@Param("article") Article article);

    void updateArticle(@Param("article") Article article);

    void updateArticleGroupId(
        @Param("groupId") Long groupId,
        @Param("articleId") Long articleId
    );

    Article findArticleByArticleId(
        @Param("articleId") Long articleId
    );

    int deleteArticleById(Long id);

    void saveArticleBlock(@Param("article") ArticleBlock articleBlock);

}
