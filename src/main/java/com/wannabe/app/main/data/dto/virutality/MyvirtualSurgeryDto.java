package com.wannabe.app.main.data.dto.virutality;

import com.wannabe.app.main.data.entity.VirtualSurgery;
import com.wannabe.app.main.data.state.VirtualSurgeryType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyvirtualSurgeryDto {

    private Long id;
    private String beforeImage;
    private String afterImage;
    private String rightImage;
    private String leftImage;
    private LocalDateTime createAt;
    private VirtualSurgeryType type;

    public static MyvirtualSurgeryDto of(VirtualSurgery virtualSurgery, String beforeImage, String afterImage, String rightImage, String leftImage) {
        return new MyvirtualSurgeryDto(
            virtualSurgery.getId(),
            beforeImage,
            afterImage,
            rightImage,
            leftImage,
            virtualSurgery.getDateCreated(),
            virtualSurgery.getType()
        );
    }
}
