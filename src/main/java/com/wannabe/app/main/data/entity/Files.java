package com.wannabe.app.main.data.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Files {

    private Long id;
    private Long groupId;
    private String originalFileName;
    private String fileName;
    private String path;
    private Long fileOrder;
    private String extension;
    private Double size;
    private String userType;
    private Long userId;
    private String updateUserType;
    private Long updateUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
