package com.wannabe.app.main.data.dto.response.event;

import com.wannabe.app.main.data.dto.event.GetEventDto;
import com.wannabe.app.main.data.entity.PromotionGroup;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionElementResponse {

    private Long id;
    private String promotionName;
    private Long totalCount;
    private List<GetEventDto> eventList;

    public static PromotionElementResponse of(PromotionGroup promotionGroup, List<GetEventDto> eventList) {
        return new PromotionElementResponse(promotionGroup.getId(), promotionGroup.getName(), eventList);
    }

    public static PromotionElementResponse of(PromotionGroup promotionGroup, Long totalCount, List<GetEventDto> eventList) {
        return new PromotionElementResponse(promotionGroup.getId(), promotionGroup.getName(), totalCount, eventList);
    }

    private PromotionElementResponse(Long id, String promotionName, List<GetEventDto> eventList) {
        this.id = id;
        this.promotionName = promotionName;
        this.eventList = eventList;
    }
}
