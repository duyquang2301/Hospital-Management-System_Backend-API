package com.wannabe.app.main.data.entity;

import com.wannabe.app.main.data.dto.request.virtuality.VirtualCategoryRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VirtualSurgeryPart {

    private Long id;
    private Long virtualId;
    private String category;
    private List<String> detailPart;

    public static VirtualSurgeryPart create(VirtualCategoryRequest request, Long virtualId) {
        return new VirtualSurgeryPart(request, virtualId);
    }

    private VirtualSurgeryPart(VirtualCategoryRequest request, Long virtualId) {
        this.virtualId = virtualId;
        this.category = request.getType();
        this.detailPart = request.getDetail();
    }
}
