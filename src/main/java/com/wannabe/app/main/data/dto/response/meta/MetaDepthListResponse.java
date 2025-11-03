package com.wannabe.app.main.data.dto.response.meta;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MetaDepthListResponse {

    private List<DepthResponse> metaList;
}
