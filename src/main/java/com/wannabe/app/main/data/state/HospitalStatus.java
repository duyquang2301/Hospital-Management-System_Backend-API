package com.wannabe.app.main.data.state;

public enum HospitalStatus {
    ACTIVITY("진료중"),
    CLOSED("진료 종료");


    private final String status;

    HospitalStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return this.status;
    }
}
