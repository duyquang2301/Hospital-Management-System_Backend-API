package com.wannabe.app.main.data.dto.request.virtuality;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VirtualCounselRequest {

    @NotNull(message = "가상 성형을 선택해 주세요.")
    private Long virtualSurgeryId;
    private String notes;
    @NotEmpty(message = "상담 받을 병원을 선택해 주세요.")
    private List<Long> selectedHospital;
    private List<String> category;
}
