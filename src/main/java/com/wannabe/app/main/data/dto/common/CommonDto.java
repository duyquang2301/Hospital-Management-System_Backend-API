package com.wannabe.app.main.data.dto.common;

import com.wannabe.app.main.data.state.DoctorStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommonDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalInfo {

        @Schema(description = "병원 아이디")
        private Long id;

        @Schema(description = "병원 썸네일 이미지")
        private String thumbNail;

        @Schema(description = "병원 이름")
        private String name;

        @Schema(description = "병원 주소")
        private String address;

        @Schema(description = "병원 상세 주소")
        private String addressDetail;

        @Schema(description = "병원 구/동 정보")
        private Region region;

        @Schema(description = "병원 전화 번호")
        private String phoneNumber;

        @Schema(description = "병원 노출 순위")
        private Long exposedRank;

        public void setThumbNail(String path) {
            this.thumbNail = path;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalShortInfo {

        @Schema(description = "병원 아이디")
        private Long id;

        @Schema(description = "병원 썸네일 이미지 그룹 아이디")
        private Long hospitalImageGroupId;

        @Schema(description = "병원 이름")
        private String name;

        @Schema(description = "병원 구/동 정보")
        private Region region;

        @Schema(description = "병원 썸네일 이미지")
        private String thumbNail;

        public void setThumbNail(String path) {
            this.thumbNail = path;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DoctorInfo {

        @Schema(description = "의사 아이디")
        private Long id;

        @Schema(description = "의사 프로필 이미지 아이디")
        private Long imageGroupId;

        @Schema(description = "의사 프로필 이미지")
        private String profileImg;

        @Schema(description = "의사 이름")
        private String name;

        @Schema(description = "직위")
        private String position;

        @Schema(description = "의사 진료 과목")
        private List<String> doctorCategories;

        @Schema(description = "의사 상태")
        private DoctorStatus state;

        public void setProfileImg(String profileImg) {
            this.profileImg = profileImg;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Region {

        @Schema(description = "시/도 정보")
        private String city;

        @Schema(description = "구/동 정보")
        private String district;

        public static Region of(String city, String district) {
            return new Region(city, district);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Categories {

        @Schema(description = "카테고리 아이디")
        private Long id;

        @Schema(description = "카테고리 이름")
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class OriginalImage {

        @Schema(description = "원본 이미지 아이디")
        private Long id;

        @Schema(description = "원본 이미지 path")
        private String img;
    }

    @Getter
    @AllArgsConstructor
    public static class AfterImage {

        @Schema(description = "원본 이미지 아이디")
        private Long id;

        @Schema(description = "원본 이미지 path")
        private String img;
    }

    @Getter
    @AllArgsConstructor
    public static class EventReviews {

        @Schema(description = "이벤트 후기 아이디")
        private Long id;

        @Schema(description = "이벤트 후기 썸네일")
        private String eventThumbNail;

        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "이벤트 후기 내용")
        private String content;

        @Schema(description = "이벤트 후기 가격")
        private Integer cost;
    }
}
