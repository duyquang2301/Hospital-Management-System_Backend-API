package com.wannabe.app.main.data.dto.request.push;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CounselPushRequest {

    private Long userId;
    private String title;
    private Long counselId;
}
