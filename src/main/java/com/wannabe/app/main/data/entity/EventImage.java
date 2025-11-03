package com.wannabe.app.main.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventImage {

    private Long id;
    private Long imageGroupId;
    private Long eventId;
    private String thumbnailYn;
    private String path;
    private Integer imageOrder;
}
