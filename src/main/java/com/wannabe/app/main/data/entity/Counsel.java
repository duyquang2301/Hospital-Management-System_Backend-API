package com.wannabe.app.main.data.entity;

import com.wannabe.app.main.data.state.CounselState;
import com.wannabe.app.main.data.state.CounselType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Counsel {

    private Long id;
    private Long userId;
    private CounselType counselType;
    private Long typeId;
    private CounselState state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private String description;
    private List<String> category;
    private Long imageGroupId;
    private Long virtualId;
    private String counselAnswer;

    public static Counsel createCounselByVirtualId(Long userId, CounselType counselType, Long typeId, String description, long virtualId) {
        return new Counsel(userId, counselType, typeId, description, virtualId);
    }

    public static Counsel createCounselByVirtualId(Long userId, CounselType counselType, Long typeId, String description, List<String> category) {
        return new Counsel(userId, counselType, typeId, description, category);
    }

    public Counsel(Long userId, CounselType counselType, Long typeId, String description, long virtualId) {
        this.userId = userId;
        this.counselType = counselType;
        this.typeId = typeId;
        this.description = description;
        this.virtualId = virtualId;
    }

    public Counsel(Long userId, CounselType counselType, Long typeId, String description, long virtualId, List<String> category) {
        this.userId = userId;
        this.counselType = counselType;
        this.typeId = typeId;
        this.description = description;
        this.virtualId = virtualId;
        this.category = category;
    }

    public Counsel(Long userId, CounselType counselType, Long typeId, String description, List<String> category) {
        this.userId = userId;
        this.counselType = counselType;
        this.typeId = typeId;
        this.description = description;
        this.category = category;
    }

    public Counsel(long userId, CounselType counselType, long typeId, String description, List<String> category, long virtualId) {
        this.userId = userId;
        this.counselType = counselType;
        this.typeId = typeId;
        this.description = description;
        this.category = category;
        this.virtualId = virtualId;
    }

    public Counsel(long userId, CounselType counselType, long typeId, String description) {
        this.userId = userId;
        this.counselType = counselType;
        this.typeId = typeId;
        this.description = description;
    }

    public boolean isHospitalCounsel() {
        return counselType.equals(CounselType.HOSPITAL) || counselType.equals(CounselType.VIRTUAL);
    }
}
