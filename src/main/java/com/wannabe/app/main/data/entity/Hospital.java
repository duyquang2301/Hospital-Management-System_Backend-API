package com.wannabe.app.main.data.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Hospital {

    private Long id;
    private String name;
    private String postCode;
    private String address;
    private String addressDetail;
    private String description;
    private String status;
    private LocalDateTime createAt;
    private LocalDateTime deletedAt;
    private String phoneNumber;
    private Integer consultCount;
    private String city;
    private String district;
    private List<String> medicalCategory;
    private Long imageGroupId;
    private List<String> features;
    private long exposedRank;

    public void increaseConsultCount() {
        this.consultCount++;
    }
}
