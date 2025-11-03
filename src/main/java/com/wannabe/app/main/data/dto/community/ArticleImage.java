package com.wannabe.app.main.data.dto.community;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleImage {

    @NotNull
    private long id;

    private String url;

    private long fileOrder;

    public static ArticleImage of(long id, String url, long fileOrder) {
        return new ArticleImage(id, url, fileOrder);
    }
}
