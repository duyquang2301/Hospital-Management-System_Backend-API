package com.wannabe.app.main.data.entity;

import com.wannabe.app.main.data.dto.common.YN;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private Long id;
    private Long articleId;
    private Long userId;
    private Long parentId;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private YN isReply;
    private YN isDeleted;
}
