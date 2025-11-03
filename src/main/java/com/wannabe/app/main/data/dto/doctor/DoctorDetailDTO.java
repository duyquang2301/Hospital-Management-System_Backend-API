package com.wannabe.app.main.data.dto.doctor;

import com.wannabe.app.main.data.dto.article.GetArticleListDto;
import com.wannabe.app.main.data.dto.article.GetReviewListDto;
import com.wannabe.app.main.data.dto.common.CommonDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDetailDTO {
    @Schema(description = "의사 아이디")
    private Long id;

    @Schema(description = "의사 프로필 이미지 그룹 아이디")
    private Long imageGroupId;

    @Schema(description = "의사 프로필 이미지")
    private String profileImg;

    @Schema(description = "의사 이름")
    private String name;

    @Schema(description = "의사 직책")
    private String position;

    @Schema(description = "시술 카테고리")
    private List<CommonDto.Categories> categories;

    @Schema(description = "의사가 소속된 병원 후기")
    private List<GetArticleListDto> reviewList;

    @Schema(description = "병원 정보")
    private CommonDto.HospitalShortInfo hospitalInfo;

    @Schema(description = "의사 약력")
    private String history;

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void setReviewList(List<GetArticleListDto> reviewList) {
        this.reviewList = reviewList;
    }
}
