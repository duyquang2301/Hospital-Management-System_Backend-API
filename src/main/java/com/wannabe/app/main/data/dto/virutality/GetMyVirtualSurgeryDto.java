package com.wannabe.app.main.data.dto.virutality;

import com.wannabe.app.main.data.entity.VirtualSurgery;
import com.wannabe.app.main.data.state.CounselState;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetMyVirtualSurgeryDto {

    private Long id;
    private String thumbNail;
    private LocalDateTime createAt;
    private CounselState state;

    public void updateThumbNail(String url) {
        this.thumbNail = url;
    }

    public static GetMyVirtualSurgeryDto of(VirtualSurgery virtualSurgery, String url, CounselState state) {
        return new GetMyVirtualSurgeryDto(
            virtualSurgery.getId(),
            url,
            virtualSurgery.getDateCreated(),
            state
        );
    }

}
