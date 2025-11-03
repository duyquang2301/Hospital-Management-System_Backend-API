package com.wannabe.app.main.data.dto.user;

import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.entity.Counsel;
import com.wannabe.app.main.data.state.CounselState;
import com.wannabe.app.main.data.state.CounselType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyCounselDto {

    private Long id;
    private String thumbNail;
    private Region region;
    private String hospitalName;
    private Long hospitalId;
    private String eventName;
    private CounselType type;
    private CounselState state;
    private LocalDateTime createdAt;

    public static MyCounselDto of(Counsel counsel, String url, String hospitalName, Long hospitalId, String eventName, Region region) {
        return new MyCounselDto(
            counsel.getId(),
            url,
            region,
            hospitalName,
            hospitalId,
            eventName,
            counsel.getCounselType(),
            counsel.getState(),
            counsel.getCreatedAt()
        );
    }
}
