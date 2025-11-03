package com.wannabe.app.main.data.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {

    private Long id;
    private Long hospitalId;
    private String name;
    private List<String> categories;
    private String position;
    private String description;
    private LocalDateTime createAt;
    private LocalDateTime deletedAt;
    private String status;
    private Long imageGroupId;
}
