package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.article.ArticleListDto;
import com.wannabe.app.main.data.dto.article.GetArticleListDto;
import com.wannabe.app.main.data.dto.article.GetReviewListDto;
import com.wannabe.app.main.data.dto.common.CommonDto.Region;
import com.wannabe.app.main.data.dto.common.YN;
import com.wannabe.app.main.data.dto.event.GetEventDto;
import com.wannabe.app.main.data.dto.hospital.HospitalDto;
import com.wannabe.app.main.data.dto.hospital.HospitalDto.GetSearchHospitalsDto;
import com.wannabe.app.main.data.dto.meta.LocationDTO;
import com.wannabe.app.main.data.dto.request.user.JoinRequest;
import com.wannabe.app.main.data.dto.request.user.UpdateUserInfoRequest;
import com.wannabe.app.main.data.dto.request.user.UpdateUserRequest;
import com.wannabe.app.main.data.dto.response.meta.DepthResponse;
import com.wannabe.app.main.data.dto.response.user.OtherUserInfoResponse;
import com.wannabe.app.main.data.dto.response.user.UserAdditionInfoResponse;
import com.wannabe.app.main.data.dto.response.user.UserDetailInfoResponse;
import com.wannabe.app.main.data.dto.response.user.UserInfoResponse;
import com.wannabe.app.main.data.dto.user.MyCounselDto;
import com.wannabe.app.main.data.dto.user.MyEventCounselDto;
import com.wannabe.app.main.data.dto.user.MyHospitalCounselDto;
import com.wannabe.app.main.data.dto.user.UserChatProfileDTO;
import com.wannabe.app.main.data.entity.Bookmark;
import com.wannabe.app.main.data.entity.Chatting;
import com.wannabe.app.main.data.entity.Counsel;
import com.wannabe.app.main.data.entity.Event;
import com.wannabe.app.main.data.entity.Files;
import com.wannabe.app.main.data.entity.Hospital;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.data.entity.VirtualSurgery;
import com.wannabe.app.main.data.state.ArticleType;
import com.wannabe.app.main.data.state.CounselType;
import com.wannabe.app.main.data.state.ReviewType;
import com.wannabe.app.main.exception.found.NotFoundCounselException;
import com.wannabe.app.main.exception.found.NotFoundUserException;
import com.wannabe.app.main.exception.found.NotFoundVirtualSurgeryException;
import com.wannabe.app.main.exception.paramter.AlreadyExistUserException;
import com.wannabe.app.main.exception.paramter.InvalidDeviceTokenException;
import com.wannabe.app.main.exception.paramter.InvalidGenderException;
import com.wannabe.app.main.exception.paramter.InvalidLocationException;
import com.wannabe.app.main.exception.paramter.InvalidNameException;
import com.wannabe.app.main.exception.paramter.InvalidNicknameException;
import com.wannabe.app.main.exception.paramter.InvalidPhoneNumberException;
import com.wannabe.app.main.exception.paramter.NotActiveUserException;
import com.wannabe.app.main.mapper.BookmarkMapper;
import com.wannabe.app.main.mapper.ChattingMapper;
import com.wannabe.app.main.mapper.CommunityMapper;
import com.wannabe.app.main.mapper.CounselMapper;
import com.wannabe.app.main.mapper.EventMapper;
import com.wannabe.app.main.mapper.FilesMapper;
import com.wannabe.app.main.mapper.HospitalMapper;
import com.wannabe.app.main.mapper.UserMapper;
import com.wannabe.app.main.mapper.VirtualMapper;
import com.wannabe.app.main.response.ListResponseDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final MetaService metaService;
    private final UserMapper userMapper;
    private final ImageService imageService;
    private final CloudFrontService cloudFrontService;
    private final HospitalService hospitalService;
    private final EventMapper eventMapper;
    private final FilesMapper filesMapper;
    private final BookmarkMapper bookmarkMapper;
    private final CommunityMapper communityMapper;
    private final HospitalMapper hospitalMapper;
    private final CounselMapper counselMapper;
    private final VirtualMapper virtualMapper;
    private final ChattingMapper chattingMapper;

    private final int MAX_LOCATION_SIZE = 3;
    private final String BLOCK_STATE = "BLOCK";

    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 사용자 정보 조회
     *
     * @param loginType 로그인 타입
     * @param loginKey  로그인 키
     * @return 사용자 정보
     */
    public User getUser(String loginType, String loginKey) {
        User findUser = findUserByLogin(loginType, loginKey);

        if (findUser != null) {
            return findUser;
        }

        throw new NotFoundUserException(logger);
    }

    /**
     * 사용자 정보 조회
     *
     * @param userId 사용자 아이디
     * @return 사용자 정보
     */
    public User getUser(long userId) {
        User findUser = findUserById(userId);
        String profileImg = Optional.ofNullable(findUser).flatMap(userInfo -> Optional.ofNullable(userInfo.getImageGroupId())
                .map(filesMapper::findFileByGroupId)
                .map(file -> cloudFrontService.generateSignedUrl(file.getPath())))
            .orElse(null);

        if (findUser != null) {
            findUser.updateProfilePath(profileImg);
            return findUser;
        }

        throw new NotFoundUserException(logger);
    }

    /**
     * 회원 가입
     *
     * @param request 회원 가입 내용 정보
     * @return 회원 아이디
     */
    public long joinUser(JoinRequest request) {
        validateJoinRequest(request);
        return insertJoinUser(request);
    }

    /**
     * 디바이스 토큰으로 사용자 조회
     *
     * @param userId      사용자 아이디
     * @param deviceToken 디바이스 토큰
     * @return 사용자 정보
     */
    public User findUserByDeviceToken(long userId, String deviceToken) {
        User condition = User.builder()
            .id(userId)
            .deviceToken(deviceToken)
            .build();

        return userMapper.findUserByDeviceToken(condition);
    }

    /**
     * 디바이스 토큰 업데이트
     *
     * @param deviceToken 디바이스 토큰
     * @param userId      사용자 아이디
     */
    public void updateDeviceToken(String deviceToken, long userId) {
        User user = getUser(userId);

        validateDeviceToken(deviceToken);

        updateDeviceToken(user, deviceToken);
    }

    /**
     * 디바이스 토큰 삭제
     *
     * @param userId 사용자 아이디
     */
    public void deleteUserDeviceToken(long userId) {
        if (getUser(userId) == null) {
            throw new NotFoundUserException(logger);
        }

        userMapper.deleteUserDeviceToken(userId);
    }

    /**
     * 사용자 프로필 조회
     *
     * @param userId 사용자 아이디
     * @return 사용자 정보
     */
    public UserInfoResponse getProfile(long userId) {
        return new UserInfoResponse(getUser(userId));
    }

    /**
     * 사용자 프로필 상세 조회
     *
     * @param userId 사용자 아이디
     * @return 사용자 상세 정보
     */
    public UserDetailInfoResponse getProfileDetail(long userId) {
        User user = getUser(userId);
        return new UserDetailInfoResponse(user);
    }

    /**
     * 사용자 추가 정보 조회
     *
     * @param userId 사용자 아이디
     * @return 사용자 추가 정보
     */
    public UserAdditionInfoResponse getUserAdditionInfo(long userId) {
        User user = getUser(userId);
        return UserAdditionInfoResponse.builder()
            .category(user.getCategory())
            .location(new DepthResponse(user.getCity(), user.getDistrict()))
            .build();
    }

    /**
     * 사용자 정보 업데이트
     *
     * @param userId  사용자 정보
     * @param request 수정할 사용자 정보
     */
    public void updateUserInfo(long userId, UpdateUserInfoRequest request) {
        User user = getUser(userId);
        validateUserInfoRequest(request);
        updateAdditionInfo(user, request);
    }

    /**
     * 닉네임 검증
     *
     * @param nickname 닉네임
     * @return boolean 닉네임 사용 가능 여부
     */
    public boolean checkNickname(String nickname) {
        return userMapper.findUserByNickname(nickname).isPresent();
    }

    /**
     * 닉네임 검증 임시 코드
     *
     * @param nickname 닉네임
     * @return boolean 닉네임 사용 가능 여부
     */
    public boolean checkNicknameTest(String nickname) {
        return isValidNickname(nickname);
    }

    /**
     * 회원 탈퇴
     *
     * @param userId 사용자 아아디
     */
    public void withdrawal(long userId) {
        User user = getUser(userId);

        if (user.isDeleted()) {
            throw new NotFoundUserException(logger);
        }

        user.updateWithdrawal(userMapper.findWithdrawalSeq());
        userMapper.withdrawal(user);
    }

    /**
     * 사용자 정보 수정
     *
     * @param userId       사용자 아이디
     * @param request      수정할 사용자 정보
     * @param profileImage 프로필 이미지
     */
    public void updateUser(long userId, UpdateUserRequest request, MultipartFile profileImage) {
        User user = getUser(userId);
        validateUpdateUserRequest(request, user);
        updateUserPersonalInfo(user, request);

        log.info("!!!!!! UserService.updateUser : profileImage is null? : {}", profileImage == null);

        if (profileImage == null) {
            return;
        }

        imageService.uploadUserProfileImage(user, profileImage);
    }

    /**
     * 다른 사용자 정보 조회
     *
     * @param userId      사용자 아이디
     * @param otherUserId 조회할 다른 사용자 아이디
     * @return 다른 사용자 정보
     */
    public OtherUserInfoResponse getOtherUserInfo(long userId, long otherUserId) {

        validateUserIds(userId, otherUserId);
         UserChatProfileDTO userChatProfileDTO = userMapper.findUserChatProfile(otherUserId)
            .orElseThrow(() -> new NotFoundUserException(logger));

        userChatProfileDTO.updateSignedUrl(cloudFrontService.generateSignedUrl(userChatProfileDTO.getProfileImageUrl()));

        Chatting chattingByUserIds = chattingMapper.findChattingByUserIds(userId, otherUserId);

        boolean isBlock = chattingMapper.findBlockUserIdListByUserId(userId)
            .stream()
            .anyMatch(blockUserId -> blockUserId.equals(otherUserId));

        if (chattingByUserIds == null && !isBlock) {
            return OtherUserInfoResponse.from(userChatProfileDTO);
        }

        if (chattingByUserIds == null) {
            return OtherUserInfoResponse.of(userChatProfileDTO, BLOCK_STATE);
        }

        if (isBlock) {
            return OtherUserInfoResponse.of(userChatProfileDTO, BLOCK_STATE);
        }

        return OtherUserInfoResponse.of(userChatProfileDTO, chattingByUserIds.getFirebaseKey());
    }

    /**
     * 사용자 아이디 검증
     *
     * @param userId       사용자 아이디
     * @param targetUserId 다른 사용자 아이디
     */
    private void validateUserIds(long userId, long targetUserId) {
        getActiveUser(userId);
        getActiveUser(targetUserId);
    }

    /**
     * 디바이스 토큰 업데이트
     *
     * @param user        사용자 정보
     * @param deviceToken 디바이스 토큰
     */
    private void updateDeviceToken(User user, String deviceToken) {
        userMapper.deleteDeviceToken(deviceToken);
        user.updateDeviceToken(deviceToken);
        userMapper.updateDeviceToken(user);
    }

    /**
     * 활성화된 사용자 조회
     *
     * @param userId 사용자 아이디
     * @return 사용자 정보
     */
    private User getActiveUser(long userId) {
        User findUser = findUserById(userId);

        if (findUser == null) {
            throw new NotFoundUserException(logger);
        }

        if (findUser.isActiveUser()) {
            return findUser;
        }

        throw new NotActiveUserException(logger);
    }

    /**
     * 사용자 추가 정보 수정
     *
     * @param user    사용자 정보
     * @param request 수정할 사용자 정보
     */
    private void updateUserPersonalInfo(User user, UpdateUserRequest request) {
        user.updateUser(request);
        userMapper.updateUserPersonalInfo(user);
    }

    /**
     * 수정할 사용자 정보 검증
     *
     * @param request 수정할 사용자 정보
     * @param user    사용자 정보
     */
    private void validateUpdateUserRequest(UpdateUserRequest request, User user) {
//        validateNickname(request.getNickname(), user);
//        validateName(request.getName());
        validateGender(request.getGender());
//        validatePhoneNumber(request.getPhoneNumber());
    }



    /**
     * 사용자 수정 정보 곰증
     *
     * @param request 사용자 수정 정보
     */
    private void validateUserInfoRequest(UpdateUserInfoRequest request) {
        validateSurgeryPart(request.getCategory());
        validateLocation(request.getLocation());
    }

    /**
     * 지역 검증
     *
     * @param location 지역 객체
     */
    private void validateLocation(LocationDTO location) {
        if (hasText(location.getCategory()) && (location.getValueList() == null || location.getValueList().isEmpty())) {
            return;
        }

        if (location.getValueList().size() > MAX_LOCATION_SIZE) {
            throw new InvalidLocationException(logger);
        }
    }

    /**
     * TODO 미사용
     * 핸드폰 번호 검증
     *
     * @param phoneNumber 핸드폰 번호
     */
    private void validatePhoneNumber(String phoneNumber) {
        if (hasText(phoneNumber)) {
            return;
        }

        throw new InvalidPhoneNumberException(logger);
    }

    /**
     * 디바이스 토큰 검증
     *
     * @param deviceToken 디바이스 토큰
     */
    private void validateDeviceToken(String deviceToken) {
        log.info("!!!!!! UserService.validateDeviceToken : deviceToken : {}", deviceToken);
        if (hasText(deviceToken)) {
            return;
        }

        throw new InvalidDeviceTokenException(logger);
    }

    /**
     * 회원 가입 한 사용자 저장
     *
     * @param request 회원 가입 정보
     * @return long 사용자 아이디
     */
    private long insertJoinUser(JoinRequest request) {
        User user = new User(request);
        insertUser(user);
        return user.getId();
    }

    /**
     * 회원 가입 요청 정보 검증
     *
     * @param request 회원 가입 요청 정보
     */
    private void validateJoinRequest(JoinRequest request) {
        validateLoginType(request);
        validateExistUser(request);
//        validateName(request.getName());
//        validateNickname(request.getNickname());
        validateLocation(request);
        validateSurgeryPart(request);
//        validateGender(request.getGender());
    }

    /**
     * TODO 미사용
     * 닉네암 검증
     *
     * @param nickname 닉네임
     */
    private void validateNickname(String nickname) {
        if (isValidNickname(nickname)) {
            return;
        }

        throw new InvalidNicknameException(logger);
    }

    /**
     * TODO 미사용
     * 닉네임 검증
     *
     * @param nickname 닉네임
     * @param user     사용자 정보
     */
    private void validateNickname(String nickname, User user) {
        if (isValidNickname(nickname) && (user.getNickname().equals(nickname))) {
            return;
        }

        throw new InvalidNicknameException(logger);
    }

    /**
     * 닉네입 검증
     *
     * @param nickname 닉네임
     * @return 닉네임 사용 가능 여부
     */
    private boolean isValidNickname(String nickname) {
        nickname = null;
        return hasText(nickname) && !checkNickname(nickname) && isValidLengthNickname(nickname) && !isSpecialString(nickname);
    }

    /**
     * 특수 문자 검증
     *
     * @param value 입력 받은 문자
     * @return 특수 문자 입력 여부
     */
    private boolean isSpecialString(String value) {
        Pattern pattern = Pattern.compile("[ !@#$%^&*(),.?\":{}|<>]");

        return pattern.matcher(value).find();
    }

    /**
     * 닉네임 길이 검증
     *
     * @param nickname 닉네임
     * @return 닉네임 길이 사용 가능 여부
     */
    private boolean isValidLengthNickname(String nickname) {
        return nickname.length() >= 2 && nickname.length() <= 20;
    }

    // TODO 미사용
    private void validateName(String name) {
        if (hasText(name)) {
            return;
        }

        throw new InvalidNameException(logger);
    }

    /**
     * 로그인 타입 검증
     *
     * @param request 회원 가입 요청 정보
     */
    private void validateLoginType(JoinRequest request) {
        metaService.validateLoginType(request.getLoginType());
    }

    /**
     * 이미 회원가입 한 사용자인지 검증
     *
     * @param request 회원 가입 요청 정보
     */
    private void validateExistUser(JoinRequest request) {
        User findUser = findUserByLogin(request.getLoginType(), request.getLoginKey());

        if (findUser == null) {
            return;
        }

        throw new AlreadyExistUserException(logger);
    }

    /**
     * 수술/상담 부위 검증
     *
     * @param request 회원 가입 요청 정보
     */
    private void validateSurgeryPart(JoinRequest request) {
        if (request.getCategory() == null || request.getCategory().isEmpty()) {
            return;
        }

        metaService.validateSurgeryPart(request.getCategory());
    }

    /**
     * 수술/상담 부위 검증
     *
     * @param category 수술/상담 부위
     */
    private void validateSurgeryPart(List<String> category) {
        if (category == null || category.isEmpty()) {
            return;
        }

        metaService.validateSurgeryPart(category);
    }

    /**
     * 관심 지역 검증
     *
     * @param request 회원 가입 요청 정보
     */
    private void validateLocation(JoinRequest request) {
        if (isEmptyLocation(request.getLocation())) {
            return;
        }

        if (request.getLocation().getValueList().size() > MAX_LOCATION_SIZE) {
            throw new InvalidLocationException(logger);
        }

        metaService.validateLocation(request.getLocation());
    }

    /**
     * 관심 지역 검증
     *
     * @param location 지역 객체
     * @return 관심 지역 사용 가능 여부
     */
    private boolean isEmptyLocation(LocationDTO location) {
        return location == null || !hasText(location.getCategory()) || location.getValueList() == null || location.getValueList().isEmpty();
    }

    /**
     * 성별 검증
     *
     * @param gender 성별
     */
    private void validateGender(String gender) {
        if (!hasText(gender)) {
            log.error("!!!!!! UserService.validateGender : empty gender");
            throw new InvalidGenderException(logger);
        }

        metaService.validateGender(gender);
    }

    /**
     * 사용자 저장
     *
     * @param user 사용자 정보
     * @return long 사용자 아이디
     */
    private long insertUser(User user) {
        userMapper.insertUser(user);

        return user.getId();
    }

    /**
     * 로그인 정보로 사용자 조회
     *
     * @param loginType 로그인 타입
     * @param loginKey  로그인 키
     * @return 사용저 정보
     */
    private User findUserByLogin(String loginType, String loginKey) {
        return userMapper.findUserByLogin(loginType, loginKey);
    }

    /**
     * 사용자 아이디로 사용자 조회
     *
     * @param userId 사용자 아이디
     * @return 사용자 정보
     */
    private User findUserById(long userId) {
        return userMapper.findUserById(userId);
    }

    /**
     * 사용자 추가 정보 업데이트
     *
     * @param user    사용자 정보
     * @param request 수정 할 사용자 추가 정보
     */
    private void updateAdditionInfo(User user, UpdateUserInfoRequest request) {
        user.updateAdditionInfo(request);
        userMapper.updateAdditionInfo(user);
    }

    private boolean hasText(String value) {
        return StringUtils.hasText(value);
    }

    /**
     * 모든 이벤트 스크랩 목록 조회
     *
     * @param userId 사용자 아이디
     * @param page   현재 페이지
     * @param size   가져올 목록 개수
     * @return ListResponseDto<GetEventDto> 스크랩 목록
     */
    public ListResponseDto<GetEventDto> getAllEventBookMarks(Long userId, int page, int size) {
        List<Bookmark> myEventBookmarkList = bookmarkMapper.findEventBookMarkByUserId(userId, page, size);

        List<GetEventDto> allBookmarkEventByEventId = myEventBookmarkList.stream()
            .map(bookmark -> eventMapper.findEventBookmarkByEventId(bookmark.getTypeId()))
            .filter(Objects::nonNull)
            .peek(event -> event.setThumbNail(cloudFrontService.generateSignedUrl(event.getThumbNail())))
            .toList();

        return ListResponseDto.of(allBookmarkEventByEventId, bookmarkMapper.countEventBookMark(userId));
    }

    /**
     * 병원 스크랩 목록 조회
     *
     * @param userId 사용자 아이디
     * @param page   현재 페이지
     * @param size   가져올 목록 개수
     * @return ListResponseDto<GetSearchHospitalsDto> 스크랩 한 병원 목록
     */
    public ListResponseDto<GetSearchHospitalsDto> getAllHospitalBookMarks(Long userId, int page, int size) {
        List<Bookmark> myHospitalBookMarkList = bookmarkMapper.findHospitalBookmarkByUserId(userId, page, size);

        List<GetSearchHospitalsDto> hospitalBookMarkList = myHospitalBookMarkList.stream()
            .map(bookmark -> hospitalMapper.findHospitalBookmarkByHospitalId(bookmark.getTypeId()))
            .filter(Objects::nonNull)
            .map(hospital -> {
                String thumbNail = cloudFrontService.generateSignedUrl(
                    hospitalService.getHospitalThumbNail(hospital.getImageGroupId()).orElse(""));

                List<HospitalDto.Event> events = eventMapper.findActiveEventByHospitalId(hospital.getId()).stream()
                    .map(event -> {
                        Files file = filesMapper.findFileByGroupId(event.getImageGroupId());
                        String eventUrl = cloudFrontService.generateSignedUrl(file.getPath());
                        return HospitalDto.Event.of(event, eventUrl);
                    }).toList();

                return GetSearchHospitalsDto.of(hospital, thumbNail, events);
            })
            .toList();
        return ListResponseDto.of(hospitalBookMarkList, bookmarkMapper.countHospitalBookMark(userId));
    }

    /**
     * 게시글 스크랩 목록
     *
     * @param userId 사용자 아이디
     * @param page   현재 페이지
     * @param size   가져올 목록 개수
     * @return ListResponseDto<ArticleListDto> 스크랩 한 게시글 목록
     */
    public ListResponseDto<ArticleListDto> getAllCommunityBookMarks(Long userId, int page, int size) {
        List<Bookmark> myCommunityBookMarkList = bookmarkMapper.findArticleBookmarkByUserId(userId, page, size);

        List<ArticleListDto> communityBookMarkList = myCommunityBookMarkList.stream()
            .map(bookmark -> communityMapper.findArticleByArticleId(bookmark.getTypeId()))
            .filter(Objects::nonNull)
            .map(article -> {
                User user = userMapper.findUserById(article.getWriterId());
                YN isAuthor = this.isAuthor(article.getWriterId(), userId);
                String profileImg = getProfileImg(user);

                List<String> image = Optional.ofNullable(article.getImageGroupId())
                    .map(filesMapper::findFileListByGroupId)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(files -> cloudFrontService.generateSignedUrl(files.getPath()))
                    .toList();

                if (ReviewType.VIRTUAL.equals(article.getReviewType())) {
                    String beforeImage = findFileAndMakeSignedUrl(article.getBeforeImageGroupId());
                    String afterImage = findFileAndMakeSignedUrl(article.getAfterImageGroupId());
                    return ArticleListDto.of(article, profileImg, user.getNickname(), image, beforeImage, afterImage, isAuthor);
                }

                return ArticleListDto.of(article, profileImg, user.getNickname(), image, "", "", isAuthor);
            })
            .toList();

        return ListResponseDto.of(communityBookMarkList, bookmarkMapper.countCommunityBookMark(userId));
    }

    /**
     * 사용자가 작성한 게시글 목록 조회
     *
     * @param page     현재 페이지
     * @param size     가져올 목록 개수
     * @param userId   조회 할 사용자 아이디
     * @param myUserId 사용자 아이디
     * @return ListResponseDto<ArticleListDto> 게시글 목록
     */
    public ListResponseDto<ArticleListDto> getUserAllContents(int page, int size, Long userId, Long myUserId) {
        List<ArticleListDto> userAllContents = userMapper.findAllArticles(page, size, userId)
            .stream()
            .map(articleList -> {
                User user = userMapper.findUserById(articleList.getWriterId());
                YN isAuthor = this.isAuthor(articleList.getWriterId(), myUserId);

                String profileImg = getProfileImg(user);

                List<String> image = Optional.ofNullable(articleList.getImageGroupId())
                    .map(filesMapper::findFileListByGroupId)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(files -> cloudFrontService.generateSignedUrl(files.getPath()))
                    .toList();

                String beforeImage = "";
                String afterImage = "";
//                Integer cost = 0;

                if (Objects.equals(ReviewType.VIRTUAL, articleList.getReviewType())) {
                    VirtualSurgery virtualSurgery = Optional.ofNullable(virtualMapper.findVirtualSurgeryById(articleList.getReviewTypeId()))
                        .orElse(new VirtualSurgery());
                    beforeImage = findFileAndMakeSignedUrl(virtualSurgery.getOriginalFileGroupId());
                    afterImage = findFileAndMakeSignedUrl(virtualSurgery.getVirtualFileGroupId());
//                    cost = activeEventById.getPrice();
                }

                return ArticleListDto.of(articleList, profileImg, user.getNickname(), image, beforeImage, afterImage, isAuthor);
            }).toList();

        return ListResponseDto.of(userAllContents, userMapper.countAll(userId, null));
    }

    /**
     * 사용자 프로필 이미지 생성
     *
     * @param user 사용자 정보
     * @return String 사용자 프로필 이미지
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
     * 회원이 작성한 일반 게시글 목록 조회
     *
     * @param page     현재 페이지
     * @param size     가져올 목록 개수
     * @param userId   조회 할 사용자 아이디
     * @param myUserId 사용자 아이디
     * @return ListResponseDto<GetArticleListDto> 회원이 작성한 일반 게시글 목록
     */
    // TODO communityService 랑 중복 코드
    public ListResponseDto<GetArticleListDto> getUserArticles(int page, int size, Long userId, Long myUserId) {
        List<GetArticleListDto> articles = userMapper.findAllNormalArticle(page, size, userId).stream().map(article -> {
            User user = userMapper.findUserById(article.getWriterId());
            YN isAuthor = this.isAuthor(article.getWriterId(), myUserId);

            String profileImg = getProfileImg(user);

            List<String> image = Optional.ofNullable(article.getImageGroupId())
                .map(filesMapper::findFileListByGroupId)
                .map(filesList -> filesList.stream()
                    .map(files -> cloudFrontService.generateSignedUrl(files.getPath()))
                    .filter(url -> url != null && !url.isEmpty())
                    .toList())
                .orElseGet(Collections::emptyList);

            return GetArticleListDto.of(article, profileImg, user.getNickname(), image, isAuthor);
        }).toList();

        return ListResponseDto.of(articles, userMapper.countAll(userId, ArticleType.ARTICLE));
    }

    /**
     * 회원이 작성한 후기 게시글 목록 조회
     *
     * @param page     현재 페이지
     * @param size     가져올 목록 개수
     * @param userId   조회 할 사용자 아이디
     * @param myUserId 사용자 아이디
     * @return ListResponseDto<GetReviewListDto> 회원이 작성한 후기 게시글 목록
     */
    public ListResponseDto<GetReviewListDto> getUserReviews(int page, int size, Long userId, Long myUserId) {
        List<GetReviewListDto> reviews = userMapper.findAllReviews(page, size, userId).stream().map(review -> {
            User user = userMapper.findUserById(userId);
            YN isAuthor = this.isAuthor(review.getWriterId(), myUserId);

            String profileImg = getProfileImg(user);

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
                    .map(file -> cloudFrontService.generateSignedUrl(file.getPath()))
                    .toList();
            }

            return GetReviewListDto.of(review, profileImg, user.getNickname(), beforeImage, afterImage, images, isAuthor);
        }).toList();
        return ListResponseDto.of(reviews, userMapper.countAll(userId, ArticleType.REVIEW));
    }

    /**
     * 게시글 수정 권한 검증
     *
     * @param writerId 작성자 아이디
     * @param userId   사용자 아이디
     * @return YN 수정 가능 여부
     */
    private YN isAuthor(long writerId, long userId) {
        return YN.of(writerId == userId);
    }

    /**
     * 이미지 그룹 아이디로 파일 조회 후 S3 Presigned Url 생성
     *
     * @param groupId 이미지 그룹 아이디
     * @return S3 Presigned Url
     */
    private String findFileAndMakeSignedUrl(Long groupId) {
        if (groupId == null) {
            return null;
        }
        Files file = filesMapper.findFileByGroupId(groupId);
        return cloudFrontService.generateSignedUrl(file.getPath());
    }

    /**
     * 사용자 포인트 조회
     *
     * @param userId 사용자 아이디
     * @return Integer 포인트
     */
    public Integer getPoint(Long userId) {
        return userMapper.findPointByUserId(userId);
    }

    /**
     * 상담 신청 내역
     *
     * @param userId 사용자 아이디
     * @param page   현재 페이지
     * @param size   가져올 목록 개수
     * @return ListResponseDto<MyCounselDto> 내 상담 신청 내역
     */
    public ListResponseDto<MyCounselDto> getAllCounselList(Long userId, int page, int size) {
        List<MyCounselDto> myCounselList = counselMapper.findAllCounselByUserId(userId, page, size)
            .stream()
            .map(counsel -> {
                String thumbNail = "";
                String eventName = "";
                Long hospitalId = this.findHospitalId(counsel.getCounselType(), counsel.getTypeId());

                Hospital hospital = hospitalMapper.getHospital(hospitalId);
                thumbNail = this.findFileAndMakeSignedUrl(hospital.getImageGroupId());

                if (CounselType.EVENT.equals(counsel.getCounselType())) {
                    Event counselEvent = eventMapper.findEventById(counsel.getTypeId());
                    thumbNail = this.findFileAndMakeSignedUrl(counselEvent.getImageGroupId());
                    eventName = counselEvent.getName();
                }

                String hospitalName = hospital.getName();
                Region region = Region.of(hospital.getCity(), hospital.getDistrict());

                return MyCounselDto.of(counsel, thumbNail, hospitalName, hospitalId, eventName, region);
            }).toList();
        return ListResponseDto.of(myCounselList, counselMapper.countAllByUserId(userId));
    }

    /**
     * 병원 아이디 조회
     *
     * @param type   상담 신청 타입
     * @param typeId 타입 아이디
     * @return Long 병원 아이디
     */
    private Long findHospitalId(CounselType type, Long typeId) {
        if (CounselType.EVENT.equals(type)) {
            Event counselEvent = eventMapper.findEventById(typeId);
            return counselEvent.getHospitalId();
        }

        if (CounselType.HOSPITAL.equals(type)) {
            return typeId;
        }
        return 0L;
    }

    /**
     * 이벤트 상담 신청 상세 조회
     *
     * @param userId    사용자 아이디
     * @param counselId 상담 아이디
     * @return MyEventCounselDto 이벤트 상담 신청 상세 정보
     */
    public MyEventCounselDto getEventCounselDetail(Long userId, Long counselId) {
        Counsel myCounsel = counselMapper.findEventCounselByUserIdAndId(userId, counselId)
            .orElseThrow(() -> new NotFoundCounselException(logger, "상담 내역이 존재 하지 않습니다."));

        User user = userMapper.findUserById(userId);

        // TODO 이벤트 상담 신청은 이미지 추가 하는 곳이 없다.
        Event counselEvent = eventMapper.findEventById(myCounsel.getTypeId());
        Long hospitalId = counselEvent.getHospitalId();
        String eventName = counselEvent.getName();
        String thumbNail = this.findFileAndMakeSignedUrl(counselEvent.getImageGroupId());

        Hospital hospital = hospitalMapper.getHospital(hospitalId);
        String hospitalName = hospital.getName();

        Region region = Region.of(hospital.getCity(), hospital.getDistrict());
        return MyEventCounselDto.of(myCounsel, thumbNail, region, hospitalName, eventName, user.getNickname(), user.getPhoneNumber());
    }

    /**
     * 병원 상담 내용 조회
     *
     * @param userId    사용자 아이디
     * @param counselId 상담 아이디
     * @return MyHospitalCounselDto 병원 상담 신청 내용
     */
    public MyHospitalCounselDto getCounsel(Long userId, Long counselId) {
        Counsel myCounsel = counselMapper.findCounselByUserIdAndId(userId, counselId)
            .orElseThrow(() -> new NotFoundCounselException(logger, "상담 내역이 존재 하지 않습니다."));

        // 사용자 조회 -> 닉네임, 폰번호
        User user = userMapper.findUserById(userId);

        Long hospitalId = myCounsel.getTypeId();
        Hospital hospital = hospitalMapper.getHospital(hospitalId);

        String hospitalImg = this.findFileAndMakeSignedUrl(hospital.getImageGroupId());

        List<String> image = new ArrayList<>();

        if (CounselType.HOSPITAL.equals(myCounsel.getCounselType())) {
            Optional.ofNullable(myCounsel.getImageGroupId())
                .ifPresent(imageGroupId -> {
                    List<Files> hospitalImage = filesMapper.findFileListByGroupId(imageGroupId);
                    image.addAll(hospitalImage.stream()
                        .map(files -> cloudFrontService.generateSignedUrl(files.getPath()))
                        .toList());
                });
        }

        String originalImage = "";
        String afterImage = "";
        String leftImage = "";
        String rightImage = "";

        if (myCounsel.getCounselType().equals(CounselType.VIRTUAL) && this.isNotNull(myCounsel.getVirtualId())) {
            VirtualSurgery virtualSurgery = virtualMapper.findVirtualSurgeryByUserId(myCounsel.getVirtualId(), userId)
                .orElseThrow(() -> new NotFoundVirtualSurgeryException(logger, "가상 성형 내역이 존재하지 않습니다."));

            originalImage = getSignedUrl(virtualSurgery.getOriginalFileGroupId());
            afterImage = getSignedUrl(virtualSurgery.getVirtualFileGroupId());
            leftImage = getSignedUrl(virtualSurgery.getVirtualLeftGroupId());
            rightImage = getSignedUrl(virtualSurgery.getVirtualRightGroupId());
        }

        return MyHospitalCounselDto.of(myCounsel, hospital, hospitalImg, user, image, originalImage, afterImage, leftImage, rightImage);
    }

    private String getSignedUrl(Long fileGroupId) {
        return isNotNull(fileGroupId) ? findFileAndMakeSignedUrl(fileGroupId) : "";
    }

    private boolean isNotNull(Long value) {
        return Optional.ofNullable(value).isPresent();
    }
}
