package com.wannabe.app.main.data.dto.request.virtuality;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VirtualCategoryRequest {

    private String type;
    private List<String> detail;

    public static VirtualCategoryRequest of(String type, List<String> detail) {
        VirtualCategoryRequest virtualCategoryRequest = new VirtualCategoryRequest();
        virtualCategoryRequest.setType(type);
        virtualCategoryRequest.setDetail(detail);
        return virtualCategoryRequest;
    }
}
