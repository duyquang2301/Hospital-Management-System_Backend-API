package com.wannabe.app.main.data.dto.hospital;

import com.wannabe.app.main.data.dto.common.CommonDto.DoctorInfo;
import com.wannabe.app.main.data.dto.common.CommonDto.HospitalInfo;
import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.entity.Hospital;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class HospitalDto {

    @Getter
    @AllArgsConstructor
    public static class GetSearchHospitalsDto {

        @Schema(description = "병원 정보")
        private HospitalInfo hospitalInfo;

        @Schema(description = "병원 상담횟수")
        private String counselCount;

        @Schema(description = "이벤트 내용")
        private List<Event> events;

        public static GetSearchHospitalsDto of(Hospital hospital, String url, List<Event> events) {
            return new GetSearchHospitalsDto(
                new HospitalInfo(
                    hospital.getId(),
                    url,
                    hospital.getName(),
                    hospital.getAddress(),
                    hospital.getAddressDetail(),
                    Region.of(hospital.getCity(), hospital.getDistrict()),
                    hospital.getPhoneNumber(),
                    hospital.getExposedRank()
                ),
                Integer.toString(hospital.getConsultCount()),
                events
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetRecommendListDto {

        @Schema(description = "병원 정보")
        private HospitalInfo hospitalInfo;

        @Schema(description = "병원 상담횟수")
        private Integer counselCount;

//    @Schema(description = "시술 내용")
//    private List<Surgery> surgeries;

        public static GetRecommendListDto of(HospitalInfo hospitalInfo, Integer counselCount) {
            return new GetRecommendListDto(
                hospitalInfo,
                counselCount
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class GetHospitalDto {

        @Schema(description = "병원 정보")
        private HospitalInfo hospitalInfo;

        @Schema(description = "북마크 여부")
        private YN isBookmark;

        @Schema(description = "병원 상담 신청 수")
        private Integer counselCount;

        @Schema(description = "병원 사진")
        private List<String> images;

        public static GetHospitalDto of(HospitalInfo hospitalInfo, YN isBookMark, Integer counselCount, List<String> images) {
            return new GetHospitalDto(
                hospitalInfo,
                isBookMark,
                counselCount,
                images
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetHospitalDetailDto {

        @Schema(description = "병원 아이디")
        private Long hospitalId;

        @Schema(description = "병원 소개문")
        private String intro;

        @Schema(description = "병원 진료시간")
        private List<Hours> hours;

        @Schema(description = "병원 카테고리")
        private List<String> hospitalCategories;

        @Schema(description = "병원 특징")
        private List<String> features;

        @Schema(description = "병원에 속한 의사 목록")
        private List<DoctorInfo> doctorInfo;

        public void setDoctorInfo(List<DoctorInfo> doctorInfo) {
            this.doctorInfo = doctorInfo;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Hours {

        @Schema(description = "요일")
        private String day;

        @Schema(description = "개점 시간")
        private String openTime;

        @Schema(description = "폐점 시간")
        private String closeTime;

        @Schema(description = "운영 여부")
        private String dayOff;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetHospitalEventsDto {

        @Schema(description = "이벤트 아이디")
        private Long id;

        @Schema(description = "이벤트 이미지 그룹 아이디")
        private Long imageGroupId;

        @Schema(description = "이벤트 썸네일")
        private String thumbNail;

        @Schema(description = "위치")
        private Region region;

        @Schema(description = "이벤트 이름")
        private String name;

        @Schema(description = "이벤트 상담 신청 수")
        private Integer counselCount;

        @Schema(description = "이벤트 가격")
        private Integer cost;

//        public static GetHospitalEventsDto of(GetHospitalEventsDto event, String url) {
//            return new GetHospitalEventsDto(
//                event.getId(),
//                url,
//                event.getRegion(),
//                event.getName(),
//                event.getCounselCount(),
//                event.getCost()
//            );
//        }

        public GetHospitalEventsDto(GetHospitalEventsDto event, String url) {
            this.id = event.getId();
            this.thumbNail = url;
            this.region = event.getRegion();
            this.name = event.getName();
            this.counselCount = event.getCounselCount();
            this.cost = event.getCost();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetHospitalReviewsDto {

        @Schema(description = "시술 후기 아이디")
        private Long id;

    }

    @Getter
    @AllArgsConstructor
    public static class HospitalBookmark {

        @Schema(description = "병원 아이디")
        private Long id;

        @Schema(description = "병원 북마크 여부")
        private String isBookMark;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Event {

        @Schema(description = "이벤트 아이디")
        private Long id;

        @Schema(description = "이벤트 썸네일 이미지")
        private String thumbNail;

        @Schema(description = "이벤트 이름")
        private String name;

        @Schema(description = "이벤트 가격")
        private Integer cost;

        public static Event of(com.wannabe.app.main.data.entity.Event event, String url) {
            return new Event(
                event.getId(),
                url,
                event.getName(),
                event.getPrice()
            );
        }
    }
}
