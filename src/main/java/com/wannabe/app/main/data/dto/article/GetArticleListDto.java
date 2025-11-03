package com.wannabe.app.main.data.dto.article;

import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.entity.Article;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetArticleListDto {

    private Long id;
    private Long writerId;
    private String profileImg;
    private String nickName;
    private LocalDateTime createAt;
    private Integer viewCount;
    private Integer commentCount;
    private List<String> image;
    private String content;
    private YN isAuthor;

    public static GetArticleListDto of(Article articleDto, String profileImg, String nickName, List<String> image, YN isAuthor) {
        return new GetArticleListDto(
            articleDto.getId(),
            articleDto.getWriterId(),
            profileImg,
            nickName,
            articleDto.getCreateAt(),
            articleDto.getViewCount(),
            articleDto.getCommentCount(),
            image,
            articleDto.getContent(),
            isAuthor
        );
    }
}
