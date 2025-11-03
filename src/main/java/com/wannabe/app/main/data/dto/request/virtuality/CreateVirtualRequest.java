package com.wannabe.app.main.data.dto.request.virtuality;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateVirtualRequest {

    private VirtualCategoryListRequest virtualCategoryListRequest;
    private String type;
}
