package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.article.GetArticleListDto;
import com.wannabe.app.main.data.dto.common.CommonDto.DoctorInfo;
import com.wannabe.app.main.data.dto.common.CommonDto.HospitalInfo;
import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.common.Filter;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.Event;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetHospitalDetailDto;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetHospitalDto;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetHospitalEventsDto;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetSearchHospitalsDto;
import com.wannabe.app.main.data.dto.request.hospital.CounselRequest;
import com.wannabe.app.main.data.entity.Bookmark;
import com.wannabe.app.main.data.entity.Counsel;
import com.wannabe.app.main.data.entity.Files;
import com.wannabe.app.main.data.entity.Hospital;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.data.entity.VirtualSurgery;
import com.wannabe.app.main.data.state.BookmarkType;
import com.wannabe.app.main.data.state.CounselType;
import com.wannabe.app.main.exception.found.NotFoundException;
import com.wannabe.app.main.exception.found.NotFoundHospitalException;
import com.wannabe.app.main.exception.paramter.InvalidParameterException;
import com.wannabe.app.main.mapper.BookmarkMapper;
import com.wannabe.app.main.mapper.CounselMapper;
import com.wannabe.app.main.mapper.DoctorMapper;
import com.wannabe.app.main.mapper.EventMapper;
import com.wannabe.app.main.mapper.FilesMapper;
import com.wannabe.app.main.mapper.HospitalMapper;
import com.wannabe.app.main.mapper.UserMapper;
import com.wannabe.app.main.response.ListResponseDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.wannabe.app.main.utility.ConverToLong.convertUserId;

@Service
@RequiredArgsConstructor
@Log4j2
public class HospitalService {

    private final HospitalMapper hospitalMapper;
    private final BookmarkMapper bookmarkMapper;
    private final CounselMapper counselMapper;
    private final FilesMapper fileMapper;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final FilesMapper filesMapper;
    private final DoctorMapper doctorMapper;
    private final CloudFrontService cloudFrontService;
    private final MetaService metaService;
    private final VirtualService virtualService;
    private final ImageService imageService;

    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 추천 병원 목록 조회
     *
     * @param filter 필터(정렬 조건, 페이징..)
     * @return ListResponseDto<GetRecommendList>
     */
    public ListResponseDto<GetSearchHospitalsDto> getRecommendHospitals(Filter filter) {
        List<GetSearchHospitalsDto> recommendHospitals = hospitalMapper.getHospitalsExposedRankFirstSorting(filter).stream()
            .map(hospital -> {
                String url = cloudFrontService.generateSignedUrl(
                    getHospitalThumbNail(hospital.getImageGroupId()).orElse(""));
                List<Event> events = eventMapper.findActiveEventByHospitalId(hospital.getId()).stream().map(event -> {
                    Files file = fileMapper.findFileByGroupId(event.getImageGroupId());
                    String eventUrl = cloudFrontService.generateSignedUrl(file.getPath());
                    return Event.of(event, eventUrl);
                }).toList();
                return GetSearchHospitalsDto.of(hospital, url, events);
            }).toList();
        return ListResponseDto.of(recommendHospitals, hospitalMapper.countAll(filter));
    }

    /**
     * 검색 병원 목록 조회
     *
     * @param filter 필터(정렬 조건, 페이징..)
     * @return ListResponseDto<GetSearchHospitalsDto>
     */
    public ListResponseDto<GetSearchHospitalsDto> getHospitals(Filter filter) {
        List<GetSearchHospitalsDto> searchHospitals = hospitalMapper.getHospitals(filter).stream()
            .map(hospital -> {
                String url = cloudFrontService.generateSignedUrl(
                    getHospitalThumbNail(hospital.getImageGroupId()).orElse(""));
                List<Event> events = eventMapper.findActiveEventByHospitalId(hospital.getId()).stream().map(event -> {
                    Files file = fileMapper.findFileByGroupId(event.getImageGroupId());
                    String eventUrl = cloudFrontService.generateSignedUrl(file.getPath());
                    return Event.of(event, eventUrl);
                }).toList();
                return GetSearchHospitalsDto.of(hospital, url, events);
            }).toList();
        return ListResponseDto.of(searchHospitals, hospitalMapper.countAll(filter));
    }

    /**
     * 병원 상세 조회
     *
     * @param hospitalId 병원 아이디
     * @return GetHospitalDto
     */
    public GetHospitalDto getHospital(long hospitalId, long userId) {
        Hospital hospital = hospitalMapper.getHospital(hospitalId);

        Optional<Bookmark> hospitalBookmark = bookmarkMapper.findBookmark(Bookmark.of(userId, hospitalId, BookmarkType.HOSPITAL.getBookmarkType()));

        String url = cloudFrontService.generateSignedUrl(getHospitalThumbNail(hospital.getImageGroupId()).orElse(""));
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

        List<String> images = Optional.ofNullable(hospital.getImageGroupId())
            .map(fileMapper::findFileListByGroupId)
            .orElse(Collections.emptyList())
            .stream()
            .map(Files::getPath)
            .filter(Objects::nonNull)
            .map(cloudFrontService::generateSignedUrl)
            .collect(Collectors.toList());

        return GetHospitalDto.of(hospitalInfo, YN.of(hospitalBookmark.isPresent()), hospital.getConsultCount(), images);
    }


    /**
     * 병원 상세 조회
     *
     * @param hospitalId 병원 아이디
     * @return GetHospitalDto
     */
    public GetHospitalDto getHospitalSingle(long hospitalId) {
        Hospital hospital = hospitalMapper.getHospital(hospitalId);

        String url = cloudFrontService.generateSignedUrl(getHospitalThumbNail(hospital.getImageGroupId()).orElse(""));
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

        List<String> images = Optional.ofNullable(hospital.getImageGroupId())
            .map(fileMapper::findFileListByGroupId)
            .orElse(Collections.emptyList())
            .stream()
            .map(Files::getPath)
            .filter(Objects::nonNull)
            .map(cloudFrontService::generateSignedUrl)
            .collect(Collectors.toList());

        return GetHospitalDto.of(hospitalInfo, YN.of(false), hospital.getConsultCount(), images);
    }

    /**
     * 이미지 그룹 아이디로 병원 썸네일 조회
     *
     * @param groupId 이미지 그룹 아이디
     * @return Optional<String> 병원 썸네일
     */
    public Optional<String> getHospitalThumbNail(Long groupId) {
        if (groupId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(hospitalMapper.getHospitalThumbNail(groupId));
    }

    /**
     * 병원 정보 조회
     *
     * @param hospitalId 병원 아이디
     * @return GetHospitalDetailDto 병원 상세 정보
     */
    public GetHospitalDetailDto getHospitalDetail(long hospitalId) {
        GetHospitalDetailDto hospitalDetail = hospitalMapper.getHospitalDetail(hospitalId);
        List<DoctorInfo> doctors = Optional.ofNullable(doctorMapper.getDoctorsByHospitalId(hospitalId)).orElse(Collections.emptyList());
        doctors.forEach(doctor -> {
            doctor.setProfileImg(
                Optional.ofNullable(doctor.getImageGroupId())
                    .map(fileMapper::findFileByGroupId)
                    .map(file -> cloudFrontService.generateSignedUrl(file.getPath()))
                    .orElse("")
            );
        });
        hospitalDetail.setDoctorInfo(doctors);

        return hospitalDetail;
    }

    /**
     * 뱡원 이벤트 목록 조회
     *
     * @param hospitalId 병원 아이디
     * @param page       현재 페이지
     * @param size       가져올 목록 개수
     * @param sort       정렬 조건
     * @param category   검색 카테고리
     * @return ListResponseDto<GetHospitalEventsDto>
     */
    public ListResponseDto<GetHospitalEventsDto> getHospitalEvents(long hospitalId, int page, int size, String sort, List<String> category) {
        List<GetHospitalEventsDto> hospitalEvents = hospitalMapper.getHospitalEvents(hospitalId, page, size, sort, category)
            .stream()
            .map(event -> {
                Files file = fileMapper.findFileByGroupId(event.getImageGroupId());
                String url = cloudFrontService.generateSignedUrl(file.getPath());
                return new GetHospitalEventsDto(event, url);
            }).toList();
        return ListResponseDto.of(hospitalEvents, eventMapper.countAllEventByHospitalId(hospitalId, category));
    }

    /**
     * 뱡원 시술 후기 목록 조회
     *
     * @param hospitalId 병원 아이디
     * @param page       현재 페이지
     * @param size       가져올 목록 개수
     * @param sort       정렬 조건
     * @param category   검색 카테고리
     * @return ListResponseDto<GetHospitalReviewsDto>
     */
    public ListResponseDto<GetArticleListDto> getHospitalReviews(long hospitalId, int page, int size, String sort, List<String> category,
        Long userId) {
        List<GetArticleListDto> articles = hospitalMapper.getEventReviewByHospitalId(hospitalId, page, size, sort, category).stream().map(article -> {
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

        return ListResponseDto.of(articles, hospitalMapper.countEventReviewByHospitalId(hospitalId, category));
    }

    /**
     * 수정 권한 검증
     *
     * @param writerId 작성자 아이디
     * @param userId   사용자 아이디
     * @return YN 수정 권한
     */
    private YN isAuthor(long writerId, long userId) {
        return YN.of(writerId == userId);
    }

    /**
     * 사용자 정보로 프로필 이미지 생성
     *
     * @param user 사용자 정보
     * @return 사용자 프로필 이미지 S3 Presigned Url 생성
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

    /**
     * 병원 북마크 등록
     *
     * @param hospitalId 병원 아이디
     * @param userId     사용자 아이디
     */
    public void createHospitalBookMark(long hospitalId, Long userId) {
        Bookmark bookmark = new Bookmark(userId, hospitalId, BookmarkType.HOSPITAL.getBookmarkType());

        bookmarkMapper.findBookmark(bookmark)
            .ifPresentOrElse(
                existBookmark -> {
                    throw new InvalidParameterException(log, "이미 존재하는 북마크입니다.");
                },
                () -> bookmarkMapper.insertBookmark(bookmark));
    }

    /**
     * 병원 북마크 해제
     *
     * @param hospitalId 병원 아이디
     * @param userId     사용자 아이디
     */
    public void deleteHospitalBookMark(long hospitalId, Long userId) {
        Bookmark bookmark = new Bookmark(userId, hospitalId, BookmarkType.HOSPITAL.getBookmarkType());

        bookmarkMapper.findBookmark(bookmark)
            .ifPresentOrElse(
                (existBookmark) -> bookmarkMapper.deleteBookmark(bookmark),
                () -> {
                    throw new InvalidParameterException(log, "이미 삭제된 북마크입니다.");
                });

    }

    /**
     * 상담 신청
     *
     * @param user         사용자 정
     * @param hospitalId   병원 아이디
     * @param request      상담 신청 내용
     * @param galleryImage 사용자가 추가한 이미지
     */
    @Transactional
    public void createCounsel(User user, long hospitalId, CounselRequest request, List<MultipartFile> galleryImage) {
        Hospital hospital = validateHospital(hospitalId);
        validateCounselRequest(request, user.getId());

        if (galleryImage == null || galleryImage.isEmpty()) {
            createCounselByVirtual(user.getId(), hospitalId, request);
            return;
        }

        long counselId = createCounsel(user.getId(), hospitalId, request);
        long groupId = imageService.uploadCounselImage(galleryImage, counselId, user.getId());
        counselMapper.updateImageGroupId(counselId, groupId);
        increaseConsultCount(hospital);
    }

    /**
     * 병원 아이디로 병원 검증
     *
     * @param hospitalId 병원 아이디
     * @return Hospital 병원 정보
     */
    public Hospital validateHospital(long hospitalId) {
        Hospital hospital = hospitalMapper.getHospital(hospitalId);

        if (hospital == null) {
            throw new NotFoundException(logger);
        }

        return hospital;
    }

    /**
     * 병원 아이디로 운영중인 병원 조회
     *
     * @param hospitalId 병원 아이디
     * @return HospitalInfo 병원 정보
     */
    public HospitalInfo getActiveHospitalInfo(long hospitalId) {
        HospitalInfo activeHospitalInfo = findActiveHospitalInfo(hospitalId);

        if (activeHospitalInfo == null) {
            logger.error(
                "!!!!!!! HospitalServce.getActiveHospitalInfo() : activeHospitalInfo is null !!!!!!! hospitalId : {}",
                hospitalId);
            throw new NotFoundHospitalException(logger);
        }

        activeHospitalInfo.setThumbNail(cloudFrontService.generateSignedUrl(activeHospitalInfo.getThumbNail()));
        return activeHospitalInfo;
    }

    /**
     * 병원 아이디로 병원 조회
     *
     * @param hospitalId 병원 아이디
     * @return GetSearchHospitalsDto 병원 정보
     */
    public GetSearchHospitalsDto getSearchHospitalsDto(long hospitalId) {
        Hospital hospital = hospitalMapper.getHospital(hospitalId);

        if (hospital == null) {
            logger.error("!!!!!!! HospitalServce.getSearchHospitalsDto() : hospital is null !!!!!!! hospitalId : {}", hospitalId);
            throw new NotFoundHospitalException(logger);
        }

        // TODO new ArrayList<> 실제 이벤트로 수정
        String url = cloudFrontService.generateSignedUrl(getHospitalThumbNail(hospital.getImageGroupId()).orElse(""));
        return GetSearchHospitalsDto.of(hospital, url, new ArrayList<>());
    }

    /**
     * 상담 신청 수 업데이트
     *
     * @param hospital 병워 정보
     */
    private void increaseConsultCount(Hospital hospital) {
        hospital.increaseConsultCount();
        updateConsultCount(hospital);
    }

    /**
     * 병원 상담 신청 수 업데이트
     *
     * @param hospital 병원 정보
     */
    private void updateConsultCount(Hospital hospital) {
        hospitalMapper.updateConsultCount(hospital);
    }

    /**
     * 상담 신청 요청 정보 검증
     *
     * @param request 상담 신청 요청 정보
     * @param userId  사용자 아이디
     */
    private void validateCounselRequest(CounselRequest request, long userId) {
        validateSurgeryPart(request.getCategory());
//        getVirtualPlasticSurgery(request.getVirtualSurgeryId(), userId);
    }

    /**
     * 수술 부위 검증
     *
     * @param surgeryPartList 수술 부위 목록
     */
    private void validateSurgeryPart(List<String> surgeryPartList) {
        metaService.validateSurgeryPart(surgeryPartList);
    }

    /**
     * TODO 미사용
     * 가상 성형 조회
     *
     * @param virtualId 가상 성형 아이디
     * @param userId    사용자 정보
     * @return VirtualSurgery 가상 성형 정보
     */
    private VirtualSurgery getVirtualPlasticSurgery(long virtualId, long userId) {
        return virtualService.getVirtualPlasticSurgery(virtualId, userId);
    }

    /**
     * 상담 신청 객체 생성
     *
     * @param userId     사용자 아이디
     * @param hospitalId 병원 아이디
     * @param request    상담 신청 요청 정보
     * @return long 상담 신청 아이디
     */
    private long createCounsel(long userId, long hospitalId, CounselRequest request) {
        Counsel counsel = Counsel.createCounselByVirtualId(
            userId,
            CounselType.HOSPITAL,
            hospitalId,
            request.getNotes(),
            request.getCategory()
        );

        counselMapper.insertHospitalCounsel(counsel);

        return counsel.getId();
    }

    /**
     * 병원 아이디로 운영 중인 병원 조회
     *
     * @param hospitalId 병원 아이디
     * @return HospitalInfo 병원 정보
     */
    private HospitalInfo findActiveHospitalInfo(long hospitalId) {
        return hospitalMapper.findActiveHospitalInfo(hospitalId);
    }

    /**
     * 가상 성형으로 상담 신청
     *
     * @param userId     사용자 아이디
     * @param hospitalId 병원 아이디
     * @param request    가상 성형 요청 정보
     */
    private void createCounselByVirtual(long userId, long hospitalId, CounselRequest request) {
        Hospital hospital = validateHospital(hospitalId);
        createCounsel(userId, hospitalId, request);

//        if (request.getVirtualSurgeryId() != null) {
//            updateCounselByVirtual(request.getVirtualSurgeryId());
//        }

        increaseConsultCount(hospital);
    }

    /**
     * TODO 미사용
     *
     * @param virtualId 가상 성형 아이디
     */
    private void updateCounselByVirtual(long virtualId) {
        virtualService.updateCounsel(virtualId);
    }

}
