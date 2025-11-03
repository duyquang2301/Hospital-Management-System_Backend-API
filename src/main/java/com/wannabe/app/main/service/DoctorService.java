package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.article.GetArticleListDto;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.doctor.DoctorDTO;
import com.wannabe.app.main.data.dto.doctor.DoctorDetailDTO;
import com.wannabe.app.main.data.entity.Files;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.mapper.DoctorMapper;
import com.wannabe.app.main.mapper.FilesMapper;
import com.wannabe.app.main.mapper.UserMapper;
import com.wannabe.app.main.response.ListResponseDto;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.wannabe.app.main.utility.ConverToLong.convertUserId;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorMapper doctorMapper;
    private final UserMapper userMapper;
    private final CloudFrontService cloudFrontService;
    private final FilesMapper filesMapper;

    /**
     * 의사 목록 조회
     *
     * @param query    검색어
     * @param city     도시
     * @param district 지역
     * @param category 카테고리
     * @param page     현재 페이지
     * @param size     가져올 목록 개수
     * @return ListResponseDto<DoctorDTO> 의사 목록
     */
    public ListResponseDto<DoctorDTO> getDoctors(String query, String city, Set<String> district, Set<String> category, int page, int size) {
        List<DoctorDTO> doctors = doctorMapper.getDoctors(query, city, district, category, page, size).stream()
            .peek(doctor -> doctor.setProfileImg(cloudFrontService.generateSignedUrl(doctor.getProfileImg()))).toList();
        return ListResponseDto.of(doctors, doctorMapper.countAll(query, city, district, category));
    }

    /**
     * 의사 상세 조회
     *
     * @param doctorId 의사 아이디
     * @param userId   사용자 아이디
     * @return DoctorDetailDTO 의사 정보
     */
    @Transactional
    public DoctorDetailDTO getDoctor(long doctorId, Long userId) {
        DoctorDetailDTO doctor = doctorMapper.getDoctor(doctorId);

        Files file = filesMapper.findFileByGroupId(doctor.getImageGroupId());
        doctor.setProfileImg(cloudFrontService.generateSignedUrl(file.getPath()));

        Files hospitalFile = filesMapper.findFileByGroupId(doctor.getHospitalInfo().getHospitalImageGroupId());
        doctor.getHospitalInfo().setThumbNail(cloudFrontService.generateSignedUrl(hospitalFile.getPath()));

        Long hospitalId = doctor.getHospitalInfo().getId();

        List<GetArticleListDto> reviewList = doctorMapper.getEventReviewByHospitalId(hospitalId).stream().map(article -> {
            User user = userMapper.findUserById(article.getWriterId());
            YN isAuthor = this.isAuthor(article.getWriterId(), convertUserId(userId));

            String profileImg = getProfileImg(user);

            List<String> image = Optional.ofNullable(article.getImageGroupId())
                .map(filesMapper::findFileListByGroupId)
                .map(filesList -> filesList.stream()
                    .map(files -> cloudFrontService.generateSignedUrl(files.getPath()))
                    .filter(url -> !url.isEmpty())
                    .toList())
                .orElseGet(Collections::emptyList);

            return GetArticleListDto.of(article, profileImg, user.getNickname(), image, isAuthor);
        }).toList();

        doctor.setReviewList(reviewList);
        return doctor;
    }

    /**
     * 글 작성자 본인 여부 확인
     *
     * @param writerId 작성사 아이디
     * @param userId   사용자 아이디
     * @return YN 본인 여부
     */
    private YN isAuthor(long writerId, long userId) {
        return YN.of(writerId == userId);
    }

    /**
     * 사용자 프로필 이미지 조회 후 Generate S3 Url
     *
     * @param user 사용자 정보
     * @return String 사용자 프로필 이미지 S3 Url
     */
    @NotNull
    private String getProfileImg(User user) {
        return Optional.ofNullable(user)
            .map(userInfo -> Optional.ofNullable(userInfo.getImageGroupId())
                .map(filesMapper::findFileByGroupId)
                .map(file -> cloudFrontService.generateSignedUrl(file.getPath()))
                .orElse(""))
            .orElse("");
    }
}
