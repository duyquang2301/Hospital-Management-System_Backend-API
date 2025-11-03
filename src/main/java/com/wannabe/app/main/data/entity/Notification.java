package com.wannabe.app.main.data.entity;

import com.wannabe.app.main.data.state.NotificationDetailType;
import com.wannabe.app.main.data.state.NotificationType;
import com.wannabe.app.main.data.state.YnColumn;
import com.wannabe.app.main.utility.StringUtil;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    private Long id;
    private Long userId;
    private String type;
    private Long typeId;
    private String detailType;
    private Long detailTypeId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private String readYn;

    public static Notification createComment(long userId, long articleId, long commentId) {
        return new Notification(
            userId,
            NotificationType.COMMUNITY.getType(),
            articleId,
            NotificationDetailType.COMMENT.getDetailType(),
            commentId);
    }

    public static Notification createReComment(long userId, long articleId, long replyId) {
        return new Notification(
            userId,
            NotificationType.COMMUNITY.getType(),
            articleId,
            NotificationDetailType.RE_COMMENT.getDetailType(),
            replyId);
    }

    public boolean isCommunityType() {
        return StringUtil.hasText(this.type) && this.type.equals(NotificationType.COMMUNITY.getType());
    }

    public boolean isGetEventName() {
        return StringUtil.hasText(this.detailType) && this.detailType.equals(NotificationDetailType.EVENT.getDetailType());
    }

    public boolean isRead() {
        return convertBoolean(this.readYn);
    }

    private boolean convertBoolean(String value) {
        return StringUtil.hasText(value) && value.equals(YnColumn.TRUE.getYnColumnValue());
    }

    private Notification (long userId, String type, long typeId, String detailType, long detailTypeId) {
        this.userId = userId;
        this.type = type;
        this.typeId = typeId;
        this.detailType = detailType;
        this.detailTypeId = detailTypeId;
        this.createdAt = LocalDateTime.now();
        this.readYn = "N";
    }
}
