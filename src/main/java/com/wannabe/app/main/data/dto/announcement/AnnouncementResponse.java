package com.wannabe.app.main.data.dto.announcement;

import com.wannabe.app.main.data.dto.home.BannerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnnouncementResponse {

    @Schema(description = "공지사항 아이디")
    private Long id;

    @Schema(description = "공지명")
    private Long title;

    @Schema(description = "공지 설명")
    private String description;

    public static AnnouncementResponse from(AnnouncementDto banner) {
        return new AnnouncementResponse(1L, 1L, "desc");
    }

}
