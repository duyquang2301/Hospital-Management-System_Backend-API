package com.wannabe.app.main.data.dto.response.event;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PromotionListResponse {

    private Long lastId;
    private List<PromotionElementResponse> promotionList;

    public static PromotionListResponse of(List<PromotionElementResponse> promotionList) {
        return new PromotionListResponse(promotionList);
    }

    public static PromotionListResponse of() {
        return new PromotionListResponse();
    }

    private PromotionListResponse() {
        this.lastId = 0L;
        this.promotionList = null;
    }

    private PromotionListResponse(List<PromotionElementResponse> promotionList) {
        this.lastId = getLastPromotionId(promotionList);
        this.promotionList = promotionList;
    }

    private Long getLastPromotionId(List<PromotionElementResponse> promotionList) {
        if (promotionList == null || promotionList.isEmpty()) {
            return 0L;
        }
        return promotionList.get(promotionList.size() - 1).getId();
    }
}
