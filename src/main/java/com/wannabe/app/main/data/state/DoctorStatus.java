package com.wannabe.app.main.data.state;

import lombok.Getter;

@Getter
public enum DoctorStatus {
    ACTIVE("ACTIVE"),
    DELETED("DELETED");


    private final String status;

    DoctorStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return this.status;
    }
}
