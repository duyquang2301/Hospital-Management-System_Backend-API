package com.wannabe.app.main.data.dto.event;

import com.wannabe.app.main.data.state.EventSort;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor
public class PromotionFilter {

    private Long promotionGroupId;
    private Integer size;
    private Integer page;
    private String sort;
    private List<String> district;
    private String city;
    private String category;
    private String categoryDetail;
    private Integer offset;

    public static PromotionFilter of(
        Long promotionGroupId,
        Integer size,
        Integer page,
        String sort,
        List<String> district,
        String city,
        String category,
        String categoryDetail
    ) {
        return new PromotionFilter(promotionGroupId, size, page, sort, district, city, category, categoryDetail);
    }

    public static PromotionFilter of(
        Integer size,
        Integer page,
        String sort,
        List<String> district,
        String city,
        String category,
        String categoryDetail
    ) {
        return new PromotionFilter(size, page, sort, district, city, category, categoryDetail);
    }

    private PromotionFilter(
        Long promotionGroupId,
        Integer size,
        Integer page,
        String sort,
        List<String> district,
        String city,
        String category,
        String categoryDetail
    ) {
        this.promotionGroupId = promotionGroupId;
        this.size = size;
        this.page = page;
        this.sort = buildSort(sort);
        this.district = district;
        this.city = city;
        this.category = category;
        this.categoryDetail = categoryDetail;
        this.offset = buildOffset(page, size);
    }

    private PromotionFilter(Integer size, Integer page, String sort, List<String> district, String city, String category, String categoryDetail) {
        this.size = size;
        this.page = page;
        this.sort = buildSort(sort);
        this.district = district;
        this.city = city;
        this.category = category;
        this.categoryDetail = categoryDetail;
        this.offset = buildOffset(page, size);
    }

    private Integer buildOffset(Integer page, Integer size) {
        if (!isValidOffset(page, size)) {
            return 0;
        }

        return (page - 1) * size;
    }

    private boolean isValidOffset(Integer page, Integer size) {
        return page != null && page > 0 && size != null && size > 0;
    }

    private String buildSort(String sort) {
        if (!StringUtils.hasText(sort)) {
            return null;
        }

        return EventSort.LATEST.getSortQueryBySortName(sort);
    }
}
