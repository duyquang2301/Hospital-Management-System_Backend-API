package com.wannabe.app.main.data.state;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum Gender {
    MAN("M"),
    WOMAN("W");

    private final String genderValue;

    Gender(String genderValue) {
        this.genderValue = genderValue;
    }

    public List<String> getGenderValueList() {
        return Arrays.stream(Gender.values())
            .map(Gender::getGenderValue)
            .toList();
    }
}
