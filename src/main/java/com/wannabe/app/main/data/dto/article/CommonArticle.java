package com.wannabe.app.main.data.dto.article;

import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.entity.Article;
import com.wannabe.app.main.data.entity.User;

public record CommonArticle(Article article, YN isBookMark, User user, String profileImg, YN isAuthor) {

}
