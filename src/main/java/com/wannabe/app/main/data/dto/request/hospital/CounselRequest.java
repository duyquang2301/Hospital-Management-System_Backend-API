package com.wannabe.app.main.data.dto.request.hospital;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CounselRequest {

    @NotEmpty(message = "상담 희망 부위를 선택해 주세요.")
    private List<String> category;
    private String notes;
}
