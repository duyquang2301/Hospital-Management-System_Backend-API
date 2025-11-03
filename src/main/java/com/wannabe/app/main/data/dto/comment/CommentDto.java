package com.wannabe.app.main.data.dto.comment;

import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.entity.Comment;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private Long userId;
    private String profileImg;
    private String nickName;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private YN isAuthor;
    private List<Reply> reply;
    private YN isDeleted;

    public static CommentDto of(Comment comment, String url, String nickName, YN isAuthor, List<Reply> reply) {
        return new CommentDto(
            comment.getId(),
            comment.getUserId(),
            url,
            nickName,
            comment.getContent(),
            comment.getCreateAt(),
            comment.getUpdateAt(),
            isAuthor,
            reply,
            comment.getIsDeleted()
        );
    }
}
