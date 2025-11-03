package com.wannabe.app.main.data.entity;

import com.wannabe.app.main.data.dto.community.request.CreateArticleRequest;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Term {

    private Long id;
    private String termCategory;
    private String dateStarted;
    private String termContent;

}
