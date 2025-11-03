package com.wannabe.app.main.data.dto.response.event;

import com.wannabe.app.main.data.dto.common.CommonDto.HospitalInfo;
import com.wannabe.app.main.data.dto.common.YN;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDetailResponse {

    private Long id;
    private String thumbnail;
    private String name;
    private Integer counselCount;
    private Integer cost;
    private HospitalInfo hospitalInfo;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> eventImage;
    private YN isBookMark;
}
