package com.wannabe.app.main.data.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    private Long id;
    private Long hospitalId;
    private String name;
    private Integer price;
    private Integer consultCount;
    private LocalDateTime dateCreated;
    private LocalDateTime dateStarted;
    private LocalDateTime dateEnd;
    private String state;
    private String content;
    private Long imageGroupId;
    private String category;
    private List<String> detailCategory;
    private String description;
    private Integer viewCount;

    public void increaseConsultCount() {
        this.consultCount = this.consultCount + 1;
    }
}
