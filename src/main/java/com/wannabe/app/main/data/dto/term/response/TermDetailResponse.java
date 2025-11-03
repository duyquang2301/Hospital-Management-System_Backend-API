package com.wannabe.app.main.data.dto.term.response;

import com.wannabe.app.main.data.dto.article.GetArticleDetailDto;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.community.ArticleImage;
import com.wannabe.app.main.data.entity.Term;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TermDetailResponse {

    private final Long id;
    private String termCategoryDp;
    private String dateStartedDp;
    private String termContentDp;


    public static TermDetailResponse from(Term term) {

        String termCategoryDp = switch (term.getTermCategory()) {
            case "TERMS_OF_SERVICE" -> "서비스 이용약관";
            case "PRIVACY_POLICY" -> "개인정보 처리방침";
            case "TERMS_OF_MARKETING" -> "마케팅 정보 동의 약관";
            default -> "서비스 이용약관";
        };

        String dateStartedDp = term.getDateStarted().substring(0, 10);
        String termContentDp = "";

        if (StringUtils.isNotEmpty(term.getTermContent())) {
            termContentDp = term.getTermContent().replaceAll("\\n", "<br/>");
        }


        return new TermDetailResponse(
            term.getId(),
            termCategoryDp,
            dateStartedDp,
            termContentDp
        );

    }

}
