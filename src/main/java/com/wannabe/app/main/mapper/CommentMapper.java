package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.dto.comment.request.CommentRequest;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.entity.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CommentMapper {

    List<Comment> findAllComment(
        @Param("page") int page,
        @Param("size") int size,
        @Param("articleId") Long articleId
    );

    List<Comment> findAllReply(@Param("parentId") Long parentId);

    Optional<Comment> findById(@Param("commentId") long commentId, @Param("userId") Long userId);

    Optional<Comment> findByIdAndArticleId(@Param("articleId") long articleId, @Param("commendId") long commentId);

    Optional<Comment> findByIdAndUserId(@Param("articleId") long articleId, @Param("commendId") long commentId, @Param("userId") long userId);

    long countAll(@Param("articleId") long articleId);

    long findCommentIdSequence();

    void createComment(
        @Param("id") Long id,
        @Param("articleId") Long articleId,
        @Param("userId") Long userId,
        @Param("commentRequest") CommentRequest commentRequest
    );

    void updateComment(@Param("commentId") long commentId, @Param("content") String content);

    void deleteComment(@Param("commentId") long commentId, @Param("state") YN state);
}
