package com.wannabe.app.main.data.dto.response.meta;

import com.wannabe.app.main.data.entity.VirtualSurgeryPart;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepthResponse {

    private String category;
    private List<String> valueList;

    public static DepthResponse buildVirtualSurgeryPart(VirtualSurgeryPart surgeryPart) {
        if (surgeryPart == null) {
            return null;
        }

        return new DepthResponse(surgeryPart.getCategory(), surgeryPart.getDetailPart());
    }

    public DepthResponse(String category) {
        this.category = category;
        this.valueList = new ArrayList<>();
    }
}
