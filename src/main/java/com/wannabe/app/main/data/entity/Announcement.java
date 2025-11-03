package com.wannabe.app.main.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {

    private Long id;
    private String title;
    private String description;
    private Long imageGroupId;

    private String image;

}
