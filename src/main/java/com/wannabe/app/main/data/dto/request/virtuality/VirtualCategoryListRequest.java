package com.wannabe.app.main.data.dto.request.virtuality;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VirtualCategoryListRequest {

    private List<VirtualCategoryRequest> category;
}
