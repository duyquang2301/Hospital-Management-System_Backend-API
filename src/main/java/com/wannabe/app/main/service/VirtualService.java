package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.request.virtuality.CreateVirtualAndroidRequest;
import com.wannabe.app.main.data.dto.request.virtuality.CreateVirtualRequest;
import com.wannabe.app.main.data.dto.request.virtuality.VirtualCategoryRequest;
import com.wannabe.app.main.data.dto.request.virtuality.VirtualCounselRequest;
import com.wannabe.app.main.data.dto.user.MyCounselDto;
import com.wannabe.app.main.data.dto.virutality.GetMyVirtualSurgeryDto;
import com.wannabe.app.main.data.dto.virutality.MyvirtualSurgeryDto;
import com.wannabe.app.main.data.dto.virutality.response.VirtualSurgeryDetailResponse;
import com.wannabe.app.main.data.entity.Counsel;
import com.wannabe.app.main.data.entity.Files;
import com.wannabe.app.main.data.entity.Hospital;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.data.entity.VirtualSurgery;
import com.wannabe.app.main.data.entity.VirtualSurgeryPart;
import com.wannabe.app.main.data.state.CounselType;
import com.wannabe.app.main.data.state.VirtualCategory;
import com.wannabe.app.main.data.state.VirtualSurgeryType;
import com.wannabe.app.main.exception.argument.IllegalArgumentException;
import com.wannabe.app.main.exception.found.NotFoundHospitalException;
import com.wannabe.app.main.exception.found.NotFoundVirtualSurgeryException;
import com.wannabe.app.main.exception.paramter.InvalidVirtualSurgeryException;
import com.wannabe.app.main.mapper.CounselMapper;
import com.wannabe.app.main.mapper.FilesMapper;
import com.wannabe.app.main.mapper.HospitalMapper;
import com.wannabe.app.main.mapper.VirtualMapper;
import com.wannabe.app.main.response.ListResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class VirtualService {

    private final CloudFrontService cloudFrontService;
    private final CounselMapper counselMapper;
    private final HospitalMapper hospitalMapper;
    private final VirtualMapper virtualMapper;
    private final FilesMapper filesMapper;

    private final ImageService imageService;
    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 가상 성형 조회
     *
     * @param id     가상 성형 아이디
     * @param userId 사용자 아이디
     * @return VirtualSurgery 가성 성형 정보
     */
    public VirtualSurgery getVirtualPlasticSurgery(long id, long userId) {
        return virtualMapper.findVirtualSurgeryByUserId(id, userId)
            .orElseThrow(() -> new NotFoundVirtualSurgeryException(logger));
    }

    /**
     * 상담 신청 업데이트
     *
     * @param id 가상 성형 아이디
     */
    public void updateCounsel(long id) {
        VirtualSurgery virtualSurgery = getVirtualPlasticSurgery(id);

        if (!virtualSurgery.convertIsCounselYn()) {
            return;
        }

        virtualSurgery.updateIsCounsel();
        updateCounselState(virtualSurgery);
    }

    /**
     * 가상 성형 정보 조회
     *
     * @param id 가상 성형 아이디
     * @return VirtualSurgery 가상 성형 정보
     */
    public VirtualSurgery getVirtualPlasticSurgery(long id) {
        VirtualSurgery virtualSurgery = findVirtualPlasticSurgery(id);

        if (virtualSurgery == null) {
            logger.error("!!!!!!! VirtualService.getVirtualPlasticSurgery - VirtualPlasticSurgery not found. id: {}", id);
            throw new NotFoundVirtualSurgeryException(logger);
        }

        return virtualSurgery;
    }

    /**
     * 가상 성형 저장
     *
     * @param userId      사용자 아이디
     * @param request     가상 성형 정보
     * @param beforeImage 가상 성형 전 이미지
     * @param afterImage  가상 성형 후 이미지
     * @return 가상 성형 아이디
     */
    @Transactional
    public long createVirtualSurgery(long userId, CreateVirtualRequest request, MultipartFile beforeImage, MultipartFile afterImage) {
        validateCreatedVirtual(request, beforeImage, afterImage);
        VirtualSurgery virtualSurgery = VirtualSurgery.create(userId, request.getType());
        virtualMapper.insertVirtualSurgery(virtualSurgery);

        long beforeGroupId = imageService.uploadBeforeVirtualSurgeryImage(userId, virtualSurgery.getId(), beforeImage);
        long afterGroupId = imageService.uploadAfterVirtualSurgeryImage(userId, virtualSurgery.getId(), afterImage);

        virtualSurgery.updateCounselImageGroupId(beforeGroupId, afterGroupId);

        virtualMapper.updateGroupIds(virtualSurgery);

        for (VirtualCategoryRequest category : request.getVirtualCategoryListRequest().getCategory()) {
            validateVirtualCategory(category);
            virtualMapper.insertVirtualCategory(VirtualSurgeryPart.create(category, virtualSurgery.getId()));
        }

        return virtualSurgery.getId();
    }

    /**
     * 가상 성형 저장 (안드로이드 전용)
     *
     * @param userId      사용자 아이디
     * @param request     가상 성형 정보
     * @return 가상 성형 아이디
     */
    @Transactional
    public long createVirtualSurgeryByAndroid(long userId, CreateVirtualAndroidRequest request) {
        validateCreatedVirtual(request);
        VirtualSurgery virtualSurgery = VirtualSurgery.create(userId, request.getType());
        virtualMapper.insertVirtualSurgery(virtualSurgery);

        long beforeGroupId = imageService.uploadBeforeVirtualSurgeryImage(userId, virtualSurgery.getId(), request.getBeforeImage());
        long afterGroupId = imageService.uploadAfterVirtualSurgeryImage(userId, virtualSurgery.getId(), request.getAfterImage());

        virtualSurgery.updateCounselImageGroupId(beforeGroupId, afterGroupId);

        virtualMapper.updateGroupIds(virtualSurgery);

        List<VirtualCategoryRequest> virtualCategoryRequests = buildVirtualCategoryList(request);

        if (virtualCategoryRequests.isEmpty()) {
            return virtualSurgery.getId();
        }

        for (VirtualCategoryRequest category : virtualCategoryRequests) {
            validateVirtualCategory(category);
            virtualMapper.insertVirtualCategory(VirtualSurgeryPart.create(category, virtualSurgery.getId()));
        }

        return virtualSurgery.getId();
    }

    public List<VirtualCategoryRequest> getVirtualCategory() {
        List<String> detailList = new ArrayList<>();

        detailList.add("뒤트임");
        detailList.add("윗입술");

        VirtualCategory 뒤트임 = VirtualCategory.EYE.getVirtualCategory("뒤트임");
        List<VirtualCategoryRequest> virtualCategoryRequestList = VirtualCategory.EYE.getVirtualCategoryRequestList(detailList);
        return virtualCategoryRequestList;
    }

    /**
     * 가상 성형 수정
     *
     * @param userId      사용자 아이디
     * @param request     추가할 가상 성형 정보
     * @param afterImage  가상 성형 후 이미지
     */
    @Transactional
    public void updateVirtualSurgery(long userId, long virtualId, CreateVirtualRequest request, MultipartFile afterImage) {
        VirtualSurgery virtualSurgery = getVirtualPlasticSurgery(virtualId, userId);

        if (virtualSurgery.convertIsCounselYn()) {
            logger.error("!!!!!!! VirtualService.updateVirtualSurgery - already counsel. virtualId: {}", virtualId);
            throw new InvalidVirtualSurgeryException(logger);
        }

        long beforeGroupId = virtualSurgery.getVirtualFileGroupId();
        long afterGroupId = imageService.uploadAfterVirtualSurgeryImage(userId, virtualSurgery.getId(), afterImage);
        virtualSurgery.updateCounselImageGroupId(virtualSurgery.getOriginalFileGroupId(), afterGroupId);
        virtualMapper.updateVirtualSurgeryImage(virtualSurgery);

        if (isEmptyVirtualCategory(request)) {
            imageService.deleteImageByGroupId(beforeGroupId);
            return;

        }

        updateVirtualCategory(virtualId, request);

        imageService.deleteImageByGroupId(beforeGroupId);
    }

    @Transactional
    public void updateVirtualSurgeryAndroid(long userId, long virtualId, CreateVirtualAndroidRequest request) {
        VirtualSurgery virtualSurgery = getVirtualPlasticSurgery(virtualId, userId);

        if (virtualSurgery.convertIsCounselYn()) {
            logger.error("!!!!!!! VirtualService.updateVirtualSurgery - already counsel. virtualId: {}", virtualId);
            throw new InvalidVirtualSurgeryException(logger);
        }

        long beforeGroupId = virtualSurgery.getVirtualFileGroupId();
        long afterGroupId = imageService.uploadAfterVirtualSurgeryImage(userId, virtualSurgery.getId(), request.getAfterImage());
        virtualSurgery.updateCounselImageGroupId(virtualSurgery.getOriginalFileGroupId(), afterGroupId);
        virtualMapper.updateVirtualSurgeryImage(virtualSurgery);

        if (isEmptyVirtualCategory(request)) {
            imageService.deleteImageByGroupId(beforeGroupId);
            return;
        }

        updateVirtualCategory(virtualId, request);

        imageService.deleteImageByGroupId(beforeGroupId);
    }

    /**
     * 가상 성형 좌,우 이미지 저장
     *
     * @param userId     사용자 아이디
     * @param virtualId  가상 성형 아이디
     * @param leftImage  가상 성형 좌 이미지
     * @param rightImage 가상 성형 우 이미지
     * @return 가상 성형 아이디
     */
    @Transactional
    public long createVirtualSurgeryOnSide(long userId, long virtualId, MultipartFile leftImage, MultipartFile rightImage) {
        validateOnSideImage(leftImage, rightImage);
        VirtualSurgery virtualSurgery = getVirtualPlasticSurgery(virtualId, userId);
        long leftGroupId = imageService.uploadVirtualSurgeryLeftImage(userId, virtualSurgery.getId(), leftImage);
        long rightGroupId = imageService.uploadVirtualSurgeryRightImage(userId, virtualSurgery.getId(), rightImage);
        virtualSurgery.updateOnSideGroupId(leftGroupId, rightGroupId);

        virtualMapper.updateOnSideGroupIds(virtualSurgery);
        return virtualSurgery.getId();
    }

    /**
     * 가상 성형 상담 신청
     *
     * @param user    사용자 정보
     * @param request 가상 성형 상담 요청 정보
     */
    @Transactional
    public void createVirtualSurgeryCounsel(User user, VirtualCounselRequest request) {
        VirtualSurgery virtualSurgery = getVirtualPlasticSurgery(request.getVirtualSurgeryId(), user.getId());
        validateHospital(request.getSelectedHospital());

        virtualSurgery.updateIsCounsel();
        updateCounselState(virtualSurgery);
        insertVirtualSurgeryCounsel(
            virtualSurgery,
            request.getSelectedHospital(),
            request.getNotes(),
            request.getVirtualSurgeryId(),
            request.getCategory()
        );
    }

    /**
     * 가상 성형 상세 정보 조회
     *
     * @param userId    사용자 아아디
     * @param virtualId 가상 성형 아이디
     * @return VirtualSurgeryDetailResponse 가상 성형 상세 정보
     */
    public VirtualSurgeryDetailResponse getVirtualSurgeryDetail(long userId, long virtualId) {
        VirtualSurgery virtualSurgery = findVirtualPlasticSurgery(virtualId, userId);
        List<VirtualSurgeryPart> virtualSurgeryPartList = virtualMapper.findVirtualSurgeryPartByVirtualId(virtualId);

        return VirtualSurgeryDetailResponse.from(
            virtualSurgery,
            buildVirtualImageUrl(virtualSurgery.getOriginalFileGroupId()),
            buildVirtualImageUrl(virtualSurgery.getVirtualFileGroupId()),
            virtualSurgeryPartList,
            isPossibleEditVirtual(userId, virtualId)
        );
    }

    /**
     * 가상 성형 저장 시 가상 성형 시술 부위 검증
     *
     * @param category 가상 성형 시술 부위
     */
    private void validateVirtualCategory(VirtualCategoryRequest category) {
        if (category == null) {
            logger.error("!!!!!!! VirtualService.validateVirtualCategory - InvalidVirtualSurgeryException. category is null");
            throw new InvalidVirtualSurgeryException(logger);
        }

        if (!StringUtils.hasText(category.getType())) {
            logger.error("!!!!!!! VirtualService.validateVirtualCategory - InvalidVirtualSurgeryException. type is empty: {}", category.getType());
            throw new InvalidVirtualSurgeryException(logger);
        }

        if (category.getDetail() == null || category.getDetail().isEmpty()) {
            logger.error("!!!!!!! VirtualService.validateVirtualCategory - InvalidVirtualSurgeryException. detail is empty: {}",
                category.getDetail());
            throw new InvalidVirtualSurgeryException(logger);
        }

        for (String detail : category.getDetail()) {
            if (!StringUtils.hasText(detail)) {
                logger.error("!!!!!!! VirtualService.validateVirtualCategory - InvalidVirtualSurgeryException. detail element is empty: {}", detail);
                throw new InvalidVirtualSurgeryException(logger);
            }
        }
    }

    /**
     * 가상 성형 편집 가능 여부
     *
     * @param userId    사용자 아이디
     * @param virtualId 가상 성형 아이디
     * @return boolean 가상 성형 편집 가능 여부
     */
    private boolean isPossibleEditVirtual(long userId, long virtualId) {
        List<Counsel> counselList = counselMapper.findCounselByVirtualId(virtualId, userId);

        return counselList == null || counselList.isEmpty();
    }

    private List<VirtualCategoryRequest> buildVirtualCategoryList(CreateVirtualAndroidRequest request) {
        if (request.getCategoryDetailList() == null || request.getCategoryDetailList().isEmpty()) {
            return new ArrayList<>();
        }

        return VirtualCategory.EYE.getVirtualCategoryRequestList(request.getCategoryDetailList());
    }

    /**
     * 이미지 그룹 아이디로 파일 조회 후 S3 Presigned Url 생성
     *
     * @param singleGroupId 이미지 그룹 아이디
     * @return String S3 Presigned Url
     */
    private String buildVirtualImageUrl(long singleGroupId) {
        Files findFiles = filesMapper.findFileByGroupId(singleGroupId);
        return cloudFrontService.generateSignedUrl(findFiles.getPath());
    }

    /**
     * 좌, 우 이미지 검증
     *
     * @param leftFile  좌 이미지
     * @param rightFile 우 이미지
     */
    private void validateOnSideImage(MultipartFile leftFile, MultipartFile rightFile) {
        if (leftFile == null || rightFile == null) {
            logger.error("!!!!!!! VirtualService.validateOnSideImage - Image not found. leftFile: {}, rightFile: {}", leftFile, rightFile);
            throw new InvalidVirtualSurgeryException(logger);
        }
    }

    /**
     * 가상 성형 저장 정보 검증
     *
     * @param request     가상 성형 저장 정보
     * @param beforeImage 가상 성형 전 이미지
     * @param afterImage  가상 성형 후 이미지
     */
    private void validateCreatedVirtual(CreateVirtualRequest request, MultipartFile beforeImage, MultipartFile afterImage) {
        if (beforeImage == null || afterImage == null || !VirtualSurgeryType.AUTO.isValidVirtualSurgeryType(request.getType())) {
            logger.error("!!!!!!! VirtualService.validateCreatedVirtual - Image not found. beforeImage: {}, afterImage: {}", beforeImage, afterImage);
            logger.error("!!!!!!! VirtualService.validateCreatedVirtual - virtual type is invalid. type: {}", request.getType());
            throw new InvalidVirtualSurgeryException(logger);
        }
    }

    private void validateCreatedVirtual(CreateVirtualAndroidRequest request) {
        if (request == null || request.getBeforeImage() == null || request.getAfterImage() == null) {
            logger.error("!!!!!!! VirtualService.validateCreatedVirtual - Image not found. beforeImage: {}, afterImage: {}", request.getBeforeImage(), request.getAfterImage());
            throw new InvalidVirtualSurgeryException(logger);
        }

        if (!VirtualSurgeryType.AUTO.isValidVirtualSurgeryType(request.getType())) {
            logger.error("!!!!!!! VirtualService.validateCreatedVirtual - virtual type is invalid. type: {}", request.getType());
            throw new InvalidVirtualSurgeryException(logger);
        }
    }

    /**
     * 가상 성형 상담 신청
     *
     * @param virtualSurgery 가상 성형 정보
     * @param hospitalIdList 병원 아이디 목록
     * @param notes          가상 성형 상담 신청 내용
     * @param groupId        가상 성형 아이디
     * @param category       가상 성형 시술 부위
     */
    private void insertVirtualSurgeryCounsel(VirtualSurgery virtualSurgery, List<Long> hospitalIdList, String notes, long groupId,
        List<String> category) {
        for (long hospitalId : hospitalIdList) {
            Counsel counsel = new Counsel(
                virtualSurgery.getUserId(),
                CounselType.VIRTUAL,
                hospitalId,
                notes,
                groupId,
                category
            );
            counselMapper.insertVirtualCounsel(counsel);
        }
    }

    /**
     * 병원 검증
     *
     * @param hospitalIdList 병원 아이디 목록
     */
    private void validateHospital(List<Long> hospitalIdList) {
        for (Long hospitalId : hospitalIdList) {
            if (hospitalMapper.findActiveHospitalInfo(hospitalId) != null) {
                continue;
            }

            logger.error("!!!!!!! VirtualService.validateHospital - Hospital not found. hospitalId: {}", hospitalId);
            throw new NotFoundHospitalException(logger);
        }
    }

    /**
     * 상담 신청 수정
     *
     * @param virtualSurgery 가상 성형 정보
     */
    private void updateCounselState(VirtualSurgery virtualSurgery) {
        virtualMapper.updateCounselState(virtualSurgery);
    }

    /**
     * 가상 성형 아이디로 가상 성형 조회
     *
     * @param id 가상 성형 아이디
     * @return VirtualSurgery 가상 성형 정보
     */
    private VirtualSurgery findVirtualPlasticSurgery(long id) {
        return virtualMapper.findVirtualSurgeryById(id);
    }

    /**
     * 가상 성형 조회
     *
     * @param id     가상 성형 아이디
     * @param userId 사용자 아이디
     * @return VirtualSurgery 가상 성형 정보
     */
    private VirtualSurgery findVirtualPlasticSurgery(long id, long userId) {
        return virtualMapper.findVirtualSurgeryByUserId(id, userId)
            .orElseThrow(() -> new NotFoundVirtualSurgeryException(logger, "가상 성형 내역이 존재하지 않습니다."));
    }

    /**
     * 나의 가상 성형 목록 조회
     *
     * @param userId 사용자 아이디
     * @param page   현재 페이지
     * @param size   가져올 목록 개수
     * @return ListResponseDto<GetMyVirtualSurgeryDto> 가상 성형 목록
     */
    public ListResponseDto<GetMyVirtualSurgeryDto> getMyVirtualSurgeries(Long userId, int page, int size) {
        List<GetMyVirtualSurgeryDto> myVirtualSurgeryList = virtualMapper.findCounselByUserId(userId, page, size);
        myVirtualSurgeryList
            .forEach(virtualSurgery -> virtualSurgery.updateThumbNail(cloudFrontService.generateSignedUrl(virtualSurgery.getThumbNail())));
        return ListResponseDto.of(myVirtualSurgeryList, virtualMapper.countAllByUserId(userId));
    }

    /**
     * 가상 성형 삭제
     *
     * @param userId    사용자 아이디
     * @param virtualId 가상 성형 아이디
     */
    public void deleteVirtualSurgery(Long userId, Long virtualId) {
        VirtualSurgery virtualSurgery = virtualMapper.findVirtualSurgeryByUserId(virtualId, userId)
            .orElseThrow(() -> new NotFoundVirtualSurgeryException(logger, "가상 성형 내역이 존재하지 않습니다."));

        List<Counsel> counsel = counselMapper.findCounselByVirtualId(virtualId, userId);

        if (ObjectUtils.isNotEmpty(counsel)) {
            throw new IllegalArgumentException(logger, "삭제 가능한 가상 성형 내역이 아닙니다.");
        }

        virtualMapper.deleteVirtualSurgeryById(virtualId);
    }

    /**
     * 나의 가상 성형 정보 상세 조회
     *
     * @param userId    사용자 아이디
     * @param virtualId 가상 성형 아이디
     * @return MyvirtualSurgeryDto 가상 성형 상세 정보
     */
    public MyvirtualSurgeryDto getMyVirtualSurgery(Long userId, Long virtualId) {
        VirtualSurgery virtualSurgery = virtualMapper.findVirtualSurgeryByUserId(virtualId, userId)
            .orElseThrow(() -> new NotFoundVirtualSurgeryException(logger, "가상 성형 내역이 존재하지 않습니다."));

        Files beforeImageFile = filesMapper.findFileByGroupId(virtualSurgery.getOriginalFileGroupId());
        String beforeImage = this.getSignedUrl(beforeImageFile.getPath());

        Files afterImageFile = filesMapper.findFileByGroupId(virtualSurgery.getVirtualFileGroupId());
        String afterImage = this.getSignedUrl(afterImageFile.getPath());

        Files rightImageFile = filesMapper.findFileByGroupId(Optional.ofNullable(virtualSurgery.getVirtualRightGroupId()).orElse(0L));
        Files leftImageFile = filesMapper.findFileByGroupId(Optional.ofNullable(virtualSurgery.getVirtualLeftGroupId()).orElse(0L));

        String rightImage = Optional.ofNullable(rightImageFile)
            .map(file -> this.getSignedUrl(file.getPath()))
            .orElse("");

        String leftImage = Optional.ofNullable(leftImageFile)
            .map(file -> this.getSignedUrl(file.getPath()))
            .orElse("");

        return MyvirtualSurgeryDto.of(virtualSurgery, beforeImage, afterImage, rightImage, leftImage);
    }

    /**
     * S3 Presigned Url 생성
     *
     * @param path 저장 주소
     * @return String S3 Presigned Url
     */
    private String getSignedUrl(String path) {
        return cloudFrontService.generateSignedUrl(path);
    }

    /**
     * 가상 성형 한 병원 목록 조회
     *
     * @param userId    사용자 아아디
     * @param virtualId 가상 성형 아이디
     * @param page      현재 페이지
     * @param size      가져올 목록 개수
     * @return ListResponseDto<MyCounselDto> 가상 성형 한 병원 목록
     */
    public ListResponseDto<MyCounselDto> getCounselHospitals(long userId, long virtualId, int page, int size) {
        List<MyCounselDto> myCounselList = counselMapper.findAllCounselByUserIdAndId(userId, virtualId, page, size)
            .stream()
            .map(counsel -> {
                Long hospitalId = counsel.getTypeId();

                Hospital hospital = hospitalMapper.getHospital(hospitalId);

                String url = this.findFileAndMakeSignedUrl(hospital.getImageGroupId());
                String hospitalName = hospital.getName();

                Region region = Region.of(hospital.getCity(), hospital.getDistrict());
                return MyCounselDto.of(counsel, url, hospitalName, hospitalId, "", region);
            }).toList();

        return ListResponseDto.of(myCounselList, counselMapper.countAllByUserIdAndVirtualId(userId, virtualId));
    }

    /**
     * 이미지 그룹 아이디로 파일 조회 후 S3 PreSigned Url 생성
     *
     * @param groupId 이미지 그룹 아이디
     * @return S3 PreSigned Url
     */
    private String findFileAndMakeSignedUrl(Long groupId) {
        if (groupId == null) {
            return "";
        }
        Files file = filesMapper.findFileByGroupId(groupId);
        return cloudFrontService.generateSignedUrl(file.getPath());
    }

    /**
     * 가상 성형 저장 카테고리 request가 빈 값인지 판별
     *
     * @param category 가상 성형 카테고리 request
     * @return 카테고리가 비었다면 true
     */
    private boolean isEmptyVirtualCategory(CreateVirtualRequest category) {
        return category == null
            || category.getVirtualCategoryListRequest() == null
            || category.getVirtualCategoryListRequest().getCategory() == null
            || category.getVirtualCategoryListRequest().getCategory().isEmpty();
    }

    private boolean isEmptyVirtualCategory(CreateVirtualAndroidRequest request) {
        return request == null
            || request.getCategoryTypeList() == null
            || request.getCategoryTypeList().isEmpty()
            || request.getCategoryDetailList() == null
            || request.getCategoryDetailList().isEmpty();
    }

    /**
     * 가상 성형 시술 부위 추가
     * @param virtualId 가상 성형 아이디
     * @param request 추가할 가상 성형 시술 부위 목록 request
     */
    private void updateVirtualCategory(long virtualId, CreateVirtualRequest request) {
        virtualMapper.deleteVirtualCategory(virtualId);
        for (VirtualCategoryRequest category : request.getVirtualCategoryListRequest().getCategory()) {
            validateVirtualCategory(category);
            virtualMapper.insertVirtualCategory(VirtualSurgeryPart.create(category, virtualId));
        }
    }

    /**
     * 가상 성형 시술 부위 추가 (안드로이드 전용)
     * @param virtualId 가상 성형 아이디
     * @param request 추가할 가상 성형 시술 부위 목록 request
     */
    private void updateVirtualCategory(long virtualId, CreateVirtualAndroidRequest request) {
        if (isEmptyVirtualCategory(request)) {
            return;
        }

        virtualMapper.deleteVirtualCategory(virtualId);
        buildVirtualCategoryList(request).forEach(category -> {
            validateVirtualCategory(category);
            virtualMapper.insertVirtualCategory(VirtualSurgeryPart.create(category, virtualId));
        });
    }

    /**
     * 안드로이드 전용 가상 성형 사진 목록 유효성 검사
     * @param imageList 안드로이드에서 보낸 가상 성형 이미지 list
     * 안드로이드에서 보낼 사진은 항상 2개
     */
    private void validateImageList(List<MultipartFile> imageList) {
        if (imageList == null || imageList.size() != 2) {
            logger.error("!!!!!!! VirtualService.validateImageList - Image not found. imageList: {}", imageList);
            throw new InvalidVirtualSurgeryException(logger);
        }
    }
}
