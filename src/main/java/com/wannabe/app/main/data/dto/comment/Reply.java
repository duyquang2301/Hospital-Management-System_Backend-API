package com.wannabe.app.main.data.dto.comment;

import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.entity.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Reply {

    private Long id;
    private Long userId;
    private String profileImg;
    private String nickName;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private YN isAuthor;
    private YN isDeleted;

    public static Reply of(Comment reply, String profileImg, String nickName, YN isAuthor) {
        return new Reply(
            reply.getId(),
            reply.getUserId(),
            profileImg,
            nickName,
            reply.getContent(),
            reply.getCreateAt(),
            reply.getUpdateAt(),
            isAuthor,
            reply.getIsDeleted()
        );
    }
}
