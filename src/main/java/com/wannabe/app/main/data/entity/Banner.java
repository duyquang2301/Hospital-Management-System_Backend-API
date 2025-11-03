package com.wannabe.app.main.data.entity;

import com.wannabe.app.main.data.state.BannerState;
import com.wannabe.app.main.data.state.BannerType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Banner {

    private long id;
    private long adminId;
    private BannerType type;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime sentAt;
    private long imageGroupId;
    private BannerState state;
    private int exposedRank;
    private String category;
    private List<String> detailCategory;
    private int viewCount;
    private String url;

    private long bannerLinkImageGroupId;
    private String bannerLinkYn;
    private String url2;
    private Long hospitalId;

    public void setThumbNail(String path) {
        this.url = path;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }
}


