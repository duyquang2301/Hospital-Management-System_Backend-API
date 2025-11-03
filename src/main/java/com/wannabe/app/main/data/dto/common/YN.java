package com.wannabe.app.main.data.dto.common;

import lombok.Getter;

@Getter
public enum YN {
    Y(true),
    N(false);

    private final boolean v;

    YN(boolean value) {
        this.v = value;
    }

    public boolean isY() {
        return this.equals(Y);
    }

    public boolean isN() {
        return this.equals(N);
    }

    public static YN of(String value) {
        return Y.name().equals(value) ? Y : N;
    }

    public static YN of(boolean value) {
        return value ? Y : N;
    }
}
