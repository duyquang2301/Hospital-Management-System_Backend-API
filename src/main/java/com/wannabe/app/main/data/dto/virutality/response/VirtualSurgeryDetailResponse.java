package com.wannabe.app.main.data.dto.virutality.response;

import com.wannabe.app.main.data.dto.response.meta.DepthResponse;
import com.wannabe.app.main.data.entity.VirtualSurgery;
import com.wannabe.app.main.data.entity.VirtualSurgeryPart;
import com.wannabe.app.main.data.state.VirtualSurgeryType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VirtualSurgeryDetailResponse {

    private Long id;
    private String beforeImage;
    private String afterImage;
    private LocalDateTime createAt;
    private VirtualSurgeryType type;
    private List<DepthResponse> categoryList;
    private Boolean isPossibleEdit;

    public static VirtualSurgeryDetailResponse from(
        VirtualSurgery virtualSurgery,
        String beforeImage,
        String afterImage,
        List<VirtualSurgeryPart> virtualSurgeryPartList,
        boolean isPossibleEdit) {
        return new VirtualSurgeryDetailResponse(
            virtualSurgery.getId(),
            beforeImage,
            afterImage,
            virtualSurgery.getDateCreated(),
            virtualSurgery.getType(),
            buildVirtualSurgeryPart(virtualSurgeryPartList),
            isPossibleEdit
        );
    }

    private static List<DepthResponse> buildVirtualSurgeryPart(List<VirtualSurgeryPart> surgeryPartList) {
        if (surgeryPartList == null || surgeryPartList.isEmpty()) {
            return null;
        }

        List<DepthResponse> depthResponseList = new ArrayList<>();

        for (VirtualSurgeryPart surgeryPart : surgeryPartList) {
            depthResponseList.add(DepthResponse.buildVirtualSurgeryPart(surgeryPart));
        }

        return depthResponseList;
    }
}
