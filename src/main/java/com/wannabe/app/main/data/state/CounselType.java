package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum CounselType {

    HOSPITAL("HOSPITAL"),
    EVENT("EVENT"),
    VIRTUAL("VIRTUAL");

    private final String counselType;

    CounselType(String _counselType) {
        this.counselType = _counselType;
    }

    public static CounselType of(String value) {
        return switch (value) {
            case "EVENT" -> CounselType.EVENT;
            case "VIRTUAL" -> CounselType.VIRTUAL;
            default -> CounselType.HOSPITAL;
        };
    }
}
