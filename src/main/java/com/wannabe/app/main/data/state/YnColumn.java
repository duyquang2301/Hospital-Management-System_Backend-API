package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum YnColumn {
    TRUE("Y"),
    FALSE("N");

    private final String ynColumnValue;

    YnColumn(String ynColumnValue) {
        this.ynColumnValue = ynColumnValue;
    }
}
