package com.wannabe.app.main.data.dto.user;

import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.entity.Counsel;
import com.wannabe.app.main.data.state.CounselState;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyEventCounselDto {

    private Long id;
    private Long eventId;
    private String thumbNail;
    private Region region;
    private String hospitalName;
    private String eventName;
    private LocalDateTime createAt;
    private CounselState state;
    private String nickName;
    private String phoneNumber;
    private String content;
    private String answer;

    public static MyEventCounselDto of(
        Counsel counsel,
        String url,
        Region region,
        String hospitalName,
        String eventName,
        String nickName,
        String phoneNumber
    ) {
        return new MyEventCounselDto(
            counsel.getId(),
            counsel.getTypeId(),
            url,
            region,
            hospitalName,
            eventName,
            counsel.getCreatedAt(),
            counsel.getState(),
            nickName,
            phoneNumber,
            counsel.getDescription(),
            counsel.getCounselAnswer()
        );
    }
}
