package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.article.GetReviewListDto;
import com.wannabe.app.main.data.dto.common.CommonDto.HospitalInfo;
import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.event.GetEventDto;
import com.wannabe.app.main.data.dto.home.BannerDto;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetRecommendListDto;
import com.wannabe.app.main.data.entity.Files;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.data.entity.VirtualSurgery;
import com.wannabe.app.main.data.state.ReviewType;
import com.wannabe.app.main.mapper.FilesMapper;
import com.wannabe.app.main.mapper.HomeMapper;
import com.wannabe.app.main.mapper.UserMapper;
import com.wannabe.app.main.mapper.VirtualMapper;
import com.wannabe.app.main.response.ListResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HomeService {

    private final HomeMapper homeMapper;
    private final UserMapper userMapper;
    private final FilesMapper filesMapper;
    private final VirtualMapper virtualMapper;
    private final CloudFrontService cloudFrontService;
    private final HospitalService hospitalService;

    /**
     * 많이 본 이벤트 목록 조회
     *
     * @param page 현재 페이지
     * @param size 가져올 목록 개수
     * @return ListResponseDto<GetEventDto> 이벤트 목록
     */
    public ListResponseDto<GetEventDto> getPopularEvents(int page, int size) {
        List<GetEventDto> allPopularEvent = homeMapper.findAllPopularEvent(page, size);
        allPopularEvent.forEach(event -> event.setThumbNail(cloudFrontService.generateSignedUrl(event.getThumbNail())));
        return ListResponseDto.from(allPopularEvent);
    }

    /**
     * 인기있는 병원 목록 조회
     *
     * @param page 현재 페이지
     * @param size 가져올 목록 개수
     * @return ListResponseDto<GetRecommendListDto> 병원 목록
     */
    public ListResponseDto<GetRecommendListDto> getPopularHospitals(int page, int size) {
        List<GetRecommendListDto> popularHospitals = homeMapper.findAllPopularHospital(page, size).stream().map(hospital -> {
            String url = cloudFrontService.generateSignedUrl(
                hospitalService.getHospitalThumbNail(hospital.getImageGroupId()).orElse(""));
            HospitalInfo hospitalInfo = new HospitalInfo(
                hospital.getId(),
                url,
                hospital.getName(),
                hospital.getAddress(),
                hospital.getAddressDetail(),
                Region.of(hospital.getCity(), hospital.getDistrict()),
                hospital.getPhoneNumber(),
                hospital.getExposedRank()
            );
            return GetRecommendListDto.of(hospitalInfo, hospital.getConsultCount());
        }).toList();
        return ListResponseDto.from(popularHospitals);
    }

    /**
     * 미리해본 시술 후기 목록 조회
     *
     * @param page 현재 페이지
     * @param size 가져올 목록 개수
     * @return ListResponseDto<GetReviewListDto> 시술 후기 목록
     */
    public ListResponseDto<GetReviewListDto> getReviews(int page, int size) {
        List<GetReviewListDto> popularReviews = homeMapper.findAllPopularReviews(page, size).stream().map(review -> {
            User user = userMapper.findUserById(review.getWriterId());

            String profileImg = Optional.ofNullable(user)
                .map(userInfo -> Optional.ofNullable(userInfo.getImageGroupId())
                    .map(filesMapper::findFileByGroupId)
                    .map(userFile -> cloudFrontService.generateSignedUrl(userFile.getPath()))
                    .orElse(null))
                .orElse(null);

            String beforeImage = "";
            String afterImage = "";
            List<String> images = new ArrayList<>();
//            Integer cost = 0;

            if (Objects.equals(ReviewType.VIRTUAL, review.getReviewType())) {
                VirtualSurgery virtualSurgery = Optional.ofNullable(virtualMapper.findVirtualSurgeryById(review.getReviewTypeId()))
                    .orElse(new VirtualSurgery());
                beforeImage = Optional.ofNullable(findFileAndMakeSignedUrl(virtualSurgery.getOriginalFileGroupId())).orElse("");
                afterImage = Optional.ofNullable(findFileAndMakeSignedUrl(virtualSurgery.getVirtualFileGroupId())).orElse("");
//                cost = Optional.ofNullable(eventMapper.findEventById(review.getReviewTypeId()).getPrice()).orElse(0);
            }
            if (Optional.ofNullable(review.getImageGroupId()).isPresent()) {
                images = filesMapper.findFileListByGroupId(review.getImageGroupId())
                    .stream()
                    .map(files -> cloudFrontService.generateSignedUrl(files.getPath()))
                    .toList();
            }
            return GetReviewListDto.of(review, profileImg, user.getNickname(), beforeImage, afterImage, images, YN.N);
        }).toList();

        return ListResponseDto.from(popularReviews);
    }

    /**
     * 이미지 그룹 아이디로 파일 조회 후 S3 Presigned Url 생성
     *
     * @param groupId 이미지 그룹 아이디
     * @return String Presigned Url
     */
    private String findFileAndMakeSignedUrl(Long groupId) {
        if (groupId == null) {
            return null;
        }
        Files file = Optional.ofNullable(filesMapper.findFileByGroupId(groupId)).orElse(new Files());
        return cloudFrontService.generateSignedUrl(file.getPath());
    }

    /**
     * 배너 목록 조회
     *
     * @return ListResponseDto<BannerDto> 배너 목록
     */
    public ListResponseDto<BannerDto> getBanners() {
        List<BannerDto> bannerList = homeMapper.findAllBanners().stream().map(banner -> {
            banner.setThumbNail(findFileAndMakeSignedUrl(banner.getImageGroupId()));
            banner.setUrl2(findFileAndMakeSignedUrl(banner.getBannerLinkImageGroupId()));
            return BannerDto.of(banner);
        }).toList();

        return ListResponseDto.from(bannerList);
    }

    /**
     * 메인 배너 목록 조회
     *
     * @return ListResponseDto<BannerDto> 배너 목록
     */
    public ListResponseDto<BannerDto> getMainBanners() {
        List<BannerDto> bannerList = homeMapper.findAllMainBanners().stream()
            .map(banner -> {
                banner.setThumbNail(findFileAndMakeSignedUrl(banner.getImageGroupId()));
                return BannerDto.of(banner);
            })
            .toList();

        return ListResponseDto.from(bannerList);
    }
}
