package com.wannabe.app.main.data.state;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum SortOrder {
    LATEST("LATEST", "date_created DESC"),
    OLDEST("OLDEST", "date_created ASC"),
    HIGH_PRICE("HIGH_PRICE", "price DESC"),
    LOW_PRICE("LOW_PRICE", "price ASC"),
    CONSULT_COUNT("CONSULT_COUNT", "consult_count DESC"),
    VIEW_COUNT("VIEW_COUNT", "view_count DESC");

    private final String field;
    private final String order;

    SortOrder(String field, String order) {
        this.field = field;
        this.order = order;
    }

    public String getSortQueryBySortName(String sortName) {
        if (!StringUtils.hasText(sortName)) {
            return OLDEST.getOrder();
        }

        return switch (sortName) {
            case "HIGH_PRICE" -> HIGH_PRICE.getOrder();
            case "LOW_PRICE" -> LOW_PRICE.getOrder();
            case "CONSULT_COUNT" -> CONSULT_COUNT.getOrder();
            case "VIEW_COUNT" -> VIEW_COUNT.getOrder();
            case "LATEST" -> LATEST.getOrder();
            default -> OLDEST.getOrder();
        };
    }
}