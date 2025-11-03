package com.wannabe.app.main.data.state;

import co.elastic.clients.elasticsearch._types.SortOrder;

public enum SortType {
    HIGH_PRICE("cost", SortOrder.Desc),
    LOW_PRICE( "cost", SortOrder.Asc),
    CONSULT_COUNT( "consult_count", SortOrder.Desc),
    LATEST("date_updated", SortOrder.Desc),
    VIEW_COUNT("view_count", SortOrder.Desc);

    private final String field;
    private final SortOrder order;

    SortType(String field, SortOrder order) {
       this.field = field;
       this.order = order;
    }

    public String fieldValue() { return this.field; }
    public SortOrder orderValue() { return this.order; }
    public static String getValue(String sort) {
        return SortType.valueOf(sort).fieldValue();
    }
    public static SortOrder getOrder(String sort) {
        return SortType.valueOf(sort).orderValue();
    }
}
