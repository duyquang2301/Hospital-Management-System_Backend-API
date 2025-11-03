package com.wannabe.app.main.data.state;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum HospitalFeature {
    CCTV("CCTV 설치"),
    RECOVERY("전담회복실"),
    NIGHT_COUNSEL("야간상담"),
    FEMALE_DOCTOR("여의사 진료"),
    ANESTHESIA("마취 전문의");

    private final String hospitalFeatureValue;

    HospitalFeature(String hospitalFeatureValue) {
        this.hospitalFeatureValue = hospitalFeatureValue;
    }

    public List<String> getHospitalFeatureValueList() {
        return Arrays.stream(HospitalFeature.values())
            .map(HospitalFeature::getHospitalFeatureValue)
            .toList();
    }
}
