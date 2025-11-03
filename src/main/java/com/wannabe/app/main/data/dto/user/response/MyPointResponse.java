package com.wannabe.app.main.data.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPointResponse {

    @Schema(description = "사용 가능 포인트")
    private final Integer totalPoint;

    public static MyPointResponse from(Integer totalPoint) {
        return new MyPointResponse(totalPoint);
    }
}
