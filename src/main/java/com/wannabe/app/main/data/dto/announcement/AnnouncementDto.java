package com.wannabe.app.main.data.dto.announcement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnnouncementDto {

    private Long id;
    private Long title;
    private Long description;
    private String image;

}
