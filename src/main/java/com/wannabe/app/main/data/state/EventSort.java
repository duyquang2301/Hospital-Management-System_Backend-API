package com.wannabe.app.main.data.state;

import com.wannabe.app.main.utility.StringUtil;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum EventSort {
    HIGH_PRICE("HIGH_PRICE", "e.price desc"),
    LOW_PRICE("LOW_PRICE", "e.price"),
    CONSULT_COUNT("CONSULT_COUNT", "e.consult_count desc"),
    LATEST("LATEST", "e.date_started desc"),
    VIEW_COUNT("VIEW_COUNT", "e.view_count desc");

    private final String sortName;
    private final String sortQuery;

    EventSort(String sortName, String sortQuery) {
        this.sortName = sortName;
        this.sortQuery = sortQuery;
    }

    public String getSortQueryBySortName(String sortName) {
        if (!StringUtils.hasText(sortName)) {
            return LATEST.getSortQuery();
        }

        return switch (sortName) {
            case "HIGH_PRICE" -> HIGH_PRICE.getSortQuery();
            case "LOW_PRICE" -> LOW_PRICE.getSortQuery();
            case "CONSULT_COUNT" -> CONSULT_COUNT.getSortQuery();
            case "VIEW_COUNT" -> VIEW_COUNT.getSortQuery();
            default -> LATEST.getSortQuery();
        };
    }
}
