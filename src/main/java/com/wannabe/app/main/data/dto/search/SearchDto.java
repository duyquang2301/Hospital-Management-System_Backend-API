package com.wannabe.app.main.data.dto.search;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.wannabe.app.main.response.ListResponseDto;
import lombok.*;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchDto {
    private ListResponseDto<SearchDto.Event> event;
    private ListResponseDto<SearchDto.Community> article;
    private ListResponseDto<SearchDto.Hospital> hospital;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Event {
        private String id;
        private String name;
        private int cost;
        private String state;
        private int consultCount;
        private String type;
        private String category;
        private String hospitalName;
        private String city;
        private String district;
        private String thumbnail;
        private String dateUpdated;

        @JsonSetter("date_updated")
        public void setDateUpdated(String dateUpdated) {
            this.dateUpdated = dateUpdated;
        }

        @JsonGetter("dateUpdated")
        public String getDateUpdated() {
            return dateUpdated;
        }

        @JsonSetter("consult_count")
        public void setConsultCount(int consultCount) {
            this.consultCount = consultCount;
        }

        @JsonGetter("consultCount")
        public int getConsultCount() {
            return consultCount;
        }

        @JsonSetter("hospital_name")
        public void setHospitalName(String hospitalName) {
            this.hospitalName = hospitalName;
        }

        @JsonGetter("hospitalName")
        public String getHospitalName() {
            return hospitalName;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Hospital {
        private int id;
        private String name;
        private String state;
        private int consultCount;
        private String city;
        private String thumbnail;
        private String district;
        private List<String> medicalCategory;
        private String dateUpdated;
        private int exposedRank;

        @JsonSetter("date_updated")
        public void setDateUpdated(String dateUpdated) {
            this.dateUpdated = dateUpdated;
        }

        @JsonGetter("dateUpdated")
        public String getDateUpdated() {
            return dateUpdated;
        }

        @JsonSetter("exposed_rank")
        public void setExposedRank(int exposedRank) {
            this.exposedRank = exposedRank;
        }

        @JsonGetter("exposedRank")
        public int getExposedRank() {
            return exposedRank;
        }

        @JsonSetter("consult_count")
        public void setConsultCount(int consultCount) {
            this.consultCount = consultCount;
        }

        @JsonGetter("consultCount")
        public int getConsultCount() {
            return consultCount;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        @JsonSetter("medical_category")
        public void setMedicalCategory(String medicalCategory)
        {
            if(ObjectUtils.isEmpty(medicalCategory)) {
                this.medicalCategory = Collections.emptyList();
            } else {
                this.medicalCategory = Arrays.stream(medicalCategory.split(",")).toList();
            }
        }

        @JsonGetter("medicalCategory")
        public List<String> getMedicalCategory() {
            return this.medicalCategory;
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Community {
        private int id;
        private String content;
        private int viewCount;
        private String dateCreated;
        private String articleType;
        private String reviewType;
        private String userName;
        private String nickname;
        private int commentCount;
        private List<String> thumbnail;
        private String profilePath;
        private String afterImage;
        private String beforeImage;
        private String dateUpdated;

        @JsonSetter("date_updated")
        public void setDateUpdated(String dateUpdated) {
            this.dateUpdated = dateUpdated;
        }

        @JsonGetter("dateUpdated")
        public String getDateUpdated() {
            return dateUpdated;
        }

        @JsonSetter("view_count")
        public void setViewCount(int viewCount) {
            this.viewCount = viewCount;
        }

        @JsonGetter("viewCount")
        public int getViewCount() {
            return viewCount;
        }

        @JsonSetter("date_created")
        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        @JsonGetter("dateCreated")
        public String getDateCreated() {
            return dateCreated;
        }

        @JsonSetter("article_type")
        public void setArticleType(String articleType) {
            this.articleType = articleType;
        }

        @JsonGetter("articleType")
        public String getArticleType() {
            return articleType;
        }

        @JsonSetter("review_type")
        public void setReviewType(String reviewType) {
            this.reviewType = reviewType;
        }

        @JsonGetter("reviewType")
        public String getReviewType() {
            return reviewType;
        }

        @JsonSetter("user_name")
        public void setUserName(String userName) {
            this.userName = userName;
        }

        @JsonGetter("userName")
        public String getUserName() {
            return userName;
        }

        @JsonSetter("comment_count")
        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        @JsonGetter("commentCount")
        public int getCommentCount() {
            return commentCount;
        }

        @JsonSetter("thumbnail")
        public void setThumbnail(String thumbnail)
        {
            if(ObjectUtils.isEmpty(thumbnail)) {
                this.thumbnail = Collections.emptyList();
            } else {
                this.thumbnail = Arrays.stream(thumbnail.split(",")).toList();
            }
        };

        @JsonGetter("thumbnail")
        public List<String> getThumbnail() {
            return this.thumbnail;
        }

        @JsonSetter("profile_path")
        public void setProfilePath(String profilePath)
        {
            this.profilePath = profilePath;
        }
        @JsonGetter("profilePath")
        public String getProfilePath() { return this.profilePath; }

        @JsonSetter("after_image")
        public void setAfterImage(String afterImage) {
            this.afterImage = afterImage;
        }

        @JsonGetter("afterImage")
        public String getAfterImage() {
            return this.afterImage;
        }

        @JsonSetter("before_image")
        public void setBeforeImage(String beforeImage) {
            this.beforeImage = beforeImage;
        }

        @JsonGetter("beforeImage")
        public String getBeforeImage() {
            return this.beforeImage;
        }

        public void setThumbnailList(List<String> list) {
            this.thumbnail = list;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchTermRanking {
        private String key;
        private Long docCount;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchTerm {
        private String query;
        private Long dateUpdated;

        public static SearchTerm create(String query) {
            return SearchDto.SearchTerm.builder()
                .query(query)
                .dateUpdated(Instant.now().getEpochSecond())
                .build();
        }
    }
}
