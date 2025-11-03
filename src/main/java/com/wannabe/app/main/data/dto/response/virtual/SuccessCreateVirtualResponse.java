package com.wannabe.app.main.data.dto.response.virtual;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessCreateVirtualResponse {

    private Long virtualId;

    public static SuccessCreateVirtualResponse of(long virtualId) {
        return new SuccessCreateVirtualResponse(virtualId);
    }
}
