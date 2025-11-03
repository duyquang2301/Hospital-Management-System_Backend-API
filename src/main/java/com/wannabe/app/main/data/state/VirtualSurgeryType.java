package com.wannabe.app.main.data.state;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum VirtualSurgeryType {

    AUTO("AUTO"),
    CUSTOM("CUSTOM"),
    MANUAL("MANUAL");

    private final String virtualSurgeryTypeValue;

    VirtualSurgeryType(String virtualSurgeryTypeValue) {
        this.virtualSurgeryTypeValue = virtualSurgeryTypeValue;
    }

    public boolean isAuto() {
        return this.equals(AUTO);
    }

    public boolean isManual() {
        return this.equals(MANUAL);
    }

    public boolean isValidVirtualSurgeryType(String inputType) {
        if (!StringUtils.hasText(inputType)) {
            return false;
        }

        if (inputType.startsWith("\"")) {
            inputType = inputType.substring(1);
        }

        if (inputType.endsWith("\"")) {
            inputType = inputType.substring(0, inputType.length() - 1);
        }

        return inputType.equals(AUTO.getVirtualSurgeryTypeValue())
            || inputType.equals(CUSTOM.getVirtualSurgeryTypeValue())
            || inputType.equals(MANUAL.getVirtualSurgeryTypeValue());
    }
}
