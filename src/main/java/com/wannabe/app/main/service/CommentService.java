package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.comment.CommentDto;
import com.wannabe.app.main.data.dto.comment.Reply;
import com.wannabe.app.main.data.dto.comment.request.CommentRequest;
import com.wannabe.app.main.data.dto.comment.request.UpdateCommentRequest;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.entity.Comment;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.exception.paramter.InvalidParameterException;
import com.wannabe.app.main.mapper.CommentMapper;
import com.wannabe.app.main.mapper.CommunityMapper;
import com.wannabe.app.main.mapper.FilesMapper;
import com.wannabe.app.main.mapper.UserMapper;
import com.wannabe.app.main.response.ListResponseDto;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final CommunityMapper communityMapper;
    private final UserMapper userMapper;
    private final FilesMapper filesMapper;
    private final CloudFrontService cloudFrontService;
    private final NotificationService notificationService;
    private final PushService pushService;

    /**
     * 댓글 목록 조회
     *
     * @param articleId 게시글 아이디
     * @param page      현재 페이지
     * @param size      가져올 사이즈
     * @param userId    사용자 아이디
     * @return ListResponseDto<CommentDto> 댓글/답글 목록
     */
    public ListResponseDto<CommentDto> getComments(Long articleId, int page, int size, Long userId) {
        List<CommentDto> comments = commentMapper.findAllComment(page, size, articleId).stream().map(comment -> {
            User user = userMapper.findUserById(comment.getUserId());
            String profileImg = this.getUserProfileImg(user);
            YN isAuthor = YN.of(comment.getUserId().equals(convertUserId(userId)));

            List<Reply> replies = this.getReply(comment.getId(), convertUserId(userId));

            return CommentDto.of(comment, profileImg, user.getNickname(), isAuthor, replies);
        }).toList();
        // TODO countAll 이 전체 개수(댓글+답글) 인데 댓글 개수로 해야만 페이징이 될거 같다.
        return ListResponseDto.of(comments, commentMapper.countAll(articleId));
    }

    /**
     * 댓글 작성
     *
     * @param articleId      게시글 아이디
     * @param userId         사용자 아이디
     * @param commentRequest 생성 할 댓글/답글 정보
     */
    @Transactional
    public void createComment(Long articleId, Long userId, CommentRequest commentRequest) {
        if (commentRequest.getIsReply().isY() && ObjectUtils.isEmpty(commentRequest.getCommentId())) {
            log.warn("댓글 ID 가 존재 하지 않습니다. articleId: {}", articleId);
            throw new InvalidParameterException(log, "댓글이 존재 하지 않습니다.");
        }

        long commentId = commentMapper.findCommentIdSequence();
        commentMapper.createComment(commentId, articleId, userId, commentRequest);
        communityMapper.updateCommentCount(articleId);

        Article article = communityMapper.findByArticleId(articleId).orElseThrow(() -> new InvalidParameterException(log, "게시글이 존재 하지 않습니다."));
        if (!Objects.equals(userId, article.getWriterId())) {
            if (commentRequest.getIsReply().isN()) {
                // 댓글
                notificationService.createNotificationByComment(article.getWriterId(), articleId, commentId);
                pushService.sendCommentPush(article.getWriterId(), articleId, article.getType());
            } else {
                // 답글
                Comment comment = commentMapper.findByIdAndArticleId(articleId, commentRequest.getCommentId())
                    .orElseThrow(() -> new InvalidParameterException(log, "댓글이 존재 하지 않습니다."));
                notificationService.createNotificationByReComment(comment.getUserId(), articleId, commentRequest.getCommentId());
                pushService.sendReCommentPush(comment.getUserId(), articleId, article.getType());
            }
        }
    }

    /**
     * 답글 목록 조회
     *
     * @param parentId 댓글 아이디
     * @param userId   사용자 아이디
     * @return List<Reply> 답글 목록
     */
    private List<Reply> getReply(Long parentId, Long userId) {
        return commentMapper.findAllReply(parentId).stream().map(comment -> {
            User user = userMapper.findUserById(comment.getUserId());
            String profileImg = this.getUserProfileImg(user);
            YN isAuthor = YN.of(comment.getUserId().equals(userId));
            return Reply.of(comment, profileImg, user.getNickname(), isAuthor);
        }).toList();
    }

    /**
     * 사용자 프로필 이미지 조회 후 Generate S3 Url
     *
     * @param user 사용자 정보
     * @return String 사용자 프로필 이미지 S3 Url
     */
    @Nullable
    private String getUserProfileImg(User user) {
        return Optional.ofNullable(user)
            .map(userInfo -> Optional.ofNullable(userInfo.getImageGroupId())
                .map(filesMapper::findFileByGroupId)
                .map(file -> cloudFrontService.generateSignedUrl(file.getPath()))
                .orElse(""))
            .orElse("");
    }

    /**
     * 댓글/답글 수정
     *
     * @param userId               사용자 아이디
     * @param articleId            게시글 아이디
     * @param updateCommentRequest 수정할 댓글/답글 내용
     */
    @Transactional
    public void updateComment(Long userId, long articleId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = commentMapper.findByIdAndUserId(articleId, updateCommentRequest.getCommentId(), userId)
            .orElseThrow(() -> new InvalidParameterException(log, "댓글이 존재 하지 않습니다."));

        if (!userId.equals(comment.getUserId())) {
            throw new InvalidParameterException(log, "댓글 작성자가 아닙니다..");
        }

        commentMapper.updateComment(updateCommentRequest.getCommentId(), updateCommentRequest.getContent());
    }

    /**
     * 댓글 / 답글 삭제
     *
     * @param userId    사용자 아이디
     * @param commentId 댓글 / 답글 아이디
     */
    @Transactional
    public void deleteComment(Long userId, long commentId) {
        Comment comment = commentMapper.findById(commentId, userId).orElseThrow(() -> new InvalidParameterException(log, "댓글이 존재 하지 않습니다."));

        if (!userId.equals(comment.getUserId())) {
            throw new InvalidParameterException(log, "댓글 작성자가 아닙니다..");
        }

        commentMapper.deleteComment(commentId, YN.Y);
    }

    private Long convertUserId(Long userId) {
        return (userId != null) ? userId : -1L;
    }
}
