package com.wannabe.app.main.data.dto.response.user;

import com.wannabe.app.main.data.dto.response.meta.DepthResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAdditionInfoResponse {

    private List<String> category;
    private DepthResponse location;
}
