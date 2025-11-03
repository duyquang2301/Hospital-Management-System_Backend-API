package com.wannabe.app.main.data.dto.common;

import com.wannabe.app.main.data.state.SortOrder;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor
public class Filter {

    private Integer size;
    private Integer page;
    private String sort;
    private String city;
    private List<String> district;
    private List<String> category;
    private String keyword;

    public static Filter of(Integer size, Integer page, String sort, String city, List<String> district, List<String> category, String keyword) {
        return new Filter(size, page, sort, city, district, category, keyword);
    }

    protected Filter(Integer size, Integer page, String sort, String city, List<String> district, List<String> category, String keyword) {
        this.size = size;
        this.page = page;
        this.sort = buildSortType(sort);
        this.city = city;
        this.district = district;
        this.category = category;
        this.keyword = keyword;
    }

    private String buildSortType(String sort) {
        if (!StringUtils.hasText(sort)) {
            return null;
        }

        return SortOrder.LATEST.getSortQueryBySortName(sort);
    }
}
