package com.wannabe.app.main.data.entity;


import com.wannabe.app.main.data.state.VirtualSurgeryType;
import com.wannabe.app.main.data.state.YnColumn;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VirtualSurgery {

    private Long id;
    private Long userId;
    private Long originalFileGroupId;
    private Long virtualFileGroupId;
    private Long virtualLeftGroupId;
    private Long virtualRightGroupId;
    private String isCounselYn;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private VirtualSurgeryType type;

    public static VirtualSurgery create(Long userId, String type) {
        return new VirtualSurgery(userId, type);
    }

    public void updateIsCounsel() {
        this.isCounselYn = YnColumn.TRUE.getYnColumnValue();
    }

    public boolean convertIsCounselYn() {
        return convertBoolean(isCounselYn);
    }

    public void updateCounselImageGroupId(long beforeId, long afterId) {
        originalFileGroupId = beforeId;
        virtualFileGroupId = afterId;
    }

    public void updateOnSideGroupId(long leftGroupId, long rightGroupId) {
        this.virtualLeftGroupId = leftGroupId;
        this.virtualRightGroupId = rightGroupId;
    }

    private VirtualSurgery(long userId, String type) {
        this.userId = userId;
        this.type = VirtualSurgeryType.valueOf(type);
    }

    private String convertYn(boolean value) {
        return value ? YnColumn.TRUE.getYnColumnValue() : YnColumn.FALSE.getYnColumnValue();
    }

    public boolean convertBoolean(String value) {
        return hasText(value) && value.equals(YnColumn.TRUE.getYnColumnValue());
    }

    private boolean hasText(String value) {
        return StringUtils.hasText(value);
    }

}
