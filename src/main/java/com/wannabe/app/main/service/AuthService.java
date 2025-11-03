package com.wannabe.app.main.service;

import com.wannabe.app.main.data.dto.auth.SocialLoginFilter;
import com.wannabe.app.main.data.dto.auth.WannabeToken;
import com.wannabe.app.main.data.dto.request.auth.LoginRequest;
import com.wannabe.app.main.data.dto.response.auth.LoginResponse;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.exception.auth.InvalidRefreshTokenException;
import com.wannabe.app.main.exception.auth.InvalidTokenException;
import com.wannabe.app.main.exception.paramter.InvalidBannedUserException;
import com.wannabe.app.main.exception.paramter.InvalidDeviceTokenException;
import com.wannabe.app.main.exception.paramter.InvalidLoginParameterException;
import com.wannabe.app.main.mapper.UserMapper;
import com.wannabe.app.main.utility.JWTUtil;
import com.wannabe.app.main.utility.constant.HeaderKey;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;

    private final RedisService redisService;
    private final MetaService metaService;
    private final UserService userService;

    private final JWTUtil jwtUtil;
    private final long CONVERT_TO_MILLISECOND = 1000;
    private final long RECENT_REFRESH_TIME_STANDARD_SECOND = 180;

    Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 로그인
     *
     * @param request 로그인 정보
     * @return LoginResponse 로그인 후 토큰 반환
     */
    public LoginResponse login(LoginRequest request) {
        validateLoginRequest(request);

        LoginResponse loginResponse = buildLoginResponse(request);
        insertTokenInRedis(loginResponse);

        if (hasText(request.getDeviceToken())) {
            updateDeviceToken(getUser(request), request.getDeviceToken());
        }

        return loginResponse;
    }

    public String getAccessTokenTest(long userId) {
        return redisService.getAccessToken(userId);
    }

    /**
     * 사용자 검증
     *
     * @param loginKey  로그인 시 사용한 로그인 Key
     * @param loginType 로그인 시 사용한 로그인 타입
     * @return 검증 여부
     */
    public boolean validateUser(String loginKey, String loginType) {
        validateSocialLogin(loginKey, loginType);
        return userMapper.findUserBySocialLoginFilter(new SocialLoginFilter(loginKey, loginType)).isPresent();
    }

    /**
     * 로그인 정보 반환
     *
     * @param userId 사용자 아이디
     * @return LoginResponse 로그인 후 토큰 반환
     */
    public LoginResponse login(long userId) {
        LoginResponse loginResponse = buildLoginResponse(userId);
        insertTokenInRedis(loginResponse);

        return loginResponse;
    }

    /**
     * 새 토큰 발급
     *
     * @param request Client 요청 정보
     * @return LoginResponse 새 토큰 반환
     */
    public LoginResponse refreshToken(HttpServletRequest request) {
        WannabeToken refreshToken = parseRefreshToken(getRefreshTokenInHeader(request));
        User user = getUser(getUserId(request));

        if (isRecentRefresh(refreshToken.getUserId())) {
            return buildLoginResponseByRedis(user);
        }

        validateRefreshToken(refreshToken);
        return buildLoginResponse(user.getId());
    }

    /**
     * 로그인 검증
     *
     * @param deviceToken 기기 토큰
     * @param request     Client 요청 정보
     * @return LoginResponse 로그인 후 토큰 반환
     */
    public LoginResponse validateLogin(String deviceToken, HttpServletRequest request) {
        long userId = getUserId(request);

        if (!isAlreadyInstall(userId, deviceToken)) {
            return new LoginResponse();
        }

        return buildLoginResponse(userId);
    }

    /**
     * 로그 아웃
     *
     * @param request Client 요청 정보
     */
    public void logout(HttpServletRequest request) {
        User user = getUser(getUserId(request));
        expireToken(user.getId());
        deleteUserDeviceToken(user);
    }

    /**
     * Client 요청 정보 에서 userId 조회
     *
     * @param request Client 요청 정보
     * @return long 사용자 아이디
     */
    public long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(HeaderKey.USER_ID);

        if (userId != null && userId > 0) {
            return userId;
        }

        logger.error("!!!!!!! AuthService.getUserId() - userId is invalid");
        throw new InvalidTokenException(logger);
    }

    /**
     * Client 요청 정보 에서 가져온 userId 로 사용자 조회
     *
     * @param request Client 요청 정보
     * @return User 사용자 정보
     */
    public User getUser(HttpServletRequest request) {
        return getUser(getUserId(request));
    }

    /**
     * 토큰 만료
     *
     * @param userId 사용자 아이디
     */
    public void expireToken(long userId) {
        redisService.expireToken(userId);
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
     * 소셜 로그인 검증
     *
     * @param socialToken 소셜 로그인 토큰
     * @param loginType   로그인 타입
     */
    private void validateSocialLogin(String socialToken, String loginType) {
        if (!hasText(socialToken)) {
            throw new InvalidLoginParameterException(logger, false);
        }

        validateLoginType(loginType);
    }

    /**
     * 앱 설치 유무
     *
     * @param userId      사용자 아이디
     * @param deviceToken 디바이스 토큰
     * @return boolean 앱 설치 유무
     */
    private boolean isAlreadyInstall(long userId, String deviceToken) {
        validateDeviceToken(deviceToken);
        User findUser = findUserByDeviceToken(userId, deviceToken);
        return findUser != null;
    }

    /**
     * 디바이스 토큰 검증
     *
     * @param deviceToken 디바이스 토큰
     */
    private void validateDeviceToken(String deviceToken) {
        if (hasText(deviceToken)) {
            return;
        }

        logger.error("!!!!!!! AuthService.validateDeviceToken() - deviceToken is empty");
        throw new InvalidDeviceTokenException(logger);
    }

    /**
     * 리프레시 토큰 검증
     *
     * @param refreshToken 리프레시 토큰
     */
    private void validateRefreshToken(WannabeToken refreshToken) {
        String redisTokenString = redisService.getRefreshToken(refreshToken.getUserId());

        if (isValidRedisToken(refreshToken, redisTokenString)) {
            return;
        }

        logger.error("!!!!!!! AuthService.validateRefreshToken() - not equal refresh token");
        throw new InvalidRefreshTokenException(logger);
    }

    /**
     * redis 토큰 검증
     *
     * @param wannabeToken 서버에서 생성한 토큰
     * @param redisToken   레디스에 저장되어 있는 토큰
     * @return boolean 검증 여부
     */
    private boolean isValidRedisToken(WannabeToken wannabeToken, String redisToken) {
        return hasText(redisToken) && redisToken.equals(wannabeToken.getToken());
    }

    /**
     * redis 토큰으로 사용자 토큰 정보 생성
     *
     * @param user 사용자 정보
     * @return LoginResponse 로그인 후 토큰 반환
     */
    private LoginResponse buildLoginResponseByRedis(User user) {
        return new LoginResponse(user, getAccessTokenInRedis(user), getRefreshTokenInRedis(user));
    }

    /**
     * refresh 토큰 검증(최근 토큰 갱신 여부 검증)
     *
     * @param userId 사용자 아이디
     * @return 토큰 만료 여부
     */
    private boolean isRecentRefresh(long userId) {
        String redisTokenString = redisService.getRefreshToken(userId);

        if (!hasText(redisTokenString)) {
            logger.error("!!!!!!! AuthService.isRecentRefresh() - redisToken is null. do login again");
            throw new InvalidRefreshTokenException(logger);
        }

        return compareRefreshTime(parseRefreshToken(redisTokenString));
    }

    /**
     * refresh 토큰 기간 검증
     *
     * @param token 토큰
     * @return 기간 만료 여부
     */
    private boolean compareRefreshTime(WannabeToken token) {
        long differenceTime = new Date().getTime() - token.getDateCreated().getTime();

        return differenceTime / CONVERT_TO_MILLISECOND <= RECENT_REFRESH_TIME_STANDARD_SECOND;
    }

    /**
     * 로그인 정보 검증
     *
     * @param request 로그인 정보
     */
    private void validateLoginRequest(LoginRequest request) {
        if (request == null || !hasText(request.getLoginKey())) {
            throw new InvalidLoginParameterException(logger, false);
        }

        validateLoginType(request.getLoginType());
    }

    /**
     * 로그인 후 토큰
     *
     * @param request 로그인 정보
     * @return LoginResponse 로그인 후 토큰 반환
     */
    private LoginResponse buildLoginResponse(LoginRequest request) {
        User loginUser = getUser(request);

        if (loginUser.isBannedUser()) {
            logger.error("!!!!!!! AuthService.buildLoginResponse() - user is banned");
            throw new InvalidBannedUserException(logger);
        }
        return new LoginResponse(loginUser, generateAccessToken(loginUser), generateRefreshToken(loginUser));
    }

    /**
     * LoginResponse 생성
     *
     * @param userId 사용자 아이디
     * @return LoginResponse 로그인 후 토큰 반환
     */
    private LoginResponse buildLoginResponse(long userId) {
        User loginUser = getUser(userId);

        if (loginUser.isBannedUser()) {
            logger.error("!!!!!!! AuthService.buildLoginResponse() - user is banned");
            throw new InvalidBannedUserException(logger);
        }

        return LoginResponse.builder()
            .userId(loginUser.getId())
            .state(loginUser.getState())
            .token(generateAccessToken(loginUser))
            .refreshToken(generateRefreshToken(loginUser))
            .build();
    }

    /**
     * redis 에 토큰 저장
     *
     * @param response 로그인 후 반환되는 토큰 객체
     */
    private void insertTokenInRedis(LoginResponse response) {
        insertAccessToken(response);
        insertRefreshToken(response);
    }

    /**
     * Access token 저장
     *
     * @param response 로그인 후 반환되는 토큰 객체
     */
    private void insertAccessToken(LoginResponse response) {
        redisService.insertAccessToken(response.getUserId(), response.getToken());
    }

    /**
     * Refresh token 저장
     *
     * @param response 로그인 후 반환되는 토큰 객체
     */
    private void insertRefreshToken(LoginResponse response) {
        redisService.insertRefreshToken(response.getUserId(), response.getRefreshToken());
    }

    /**
     * Access token 생성
     *
     * @param user 사용자 정보
     * @return String Access token
     */
    private String generateAccessToken(User user) {
        return jwtUtil.generateAccessToken(user.getId());
    }

    /**
     * Refresh token 생성
     *
     * @param user 사용자 정보
     * @return String Refresh token
     */
    private String generateRefreshToken(User user) {
        return jwtUtil.generateRefreshToken(user.getId());
    }

    /**
     * 로그인 타입 검증
     *
     * @param loginType 로그인 타입
     */
    private void validateLoginType(String loginType) {
        metaService.validateLoginType(loginType);
    }

    /**
     * 사용자 아이디로 사용자 조회
     *
     * @param userId 사용자 아이디
     * @return User 사용자 정보
     */
    private User getUser(long userId) {
        return userService.getUser(userId);
    }

    /**
     * 로그인 정보로 사용자 조회
     *
     * @param request 로그인 정보
     * @return User 사용자 정보
     */
    private User getUser(LoginRequest request) {
        return userService.getUser(request.getLoginType(), request.getLoginKey());
    }

    /**
     * Client 요청 헤더에 있는 토큰 조회
     *
     * @param request Client 요청
     * @return String 토큰
     */
    private String getRefreshTokenInHeader(HttpServletRequest request) {
        String tokenString = request.getHeader(HeaderKey.AUTHORIZATION);

        if (hasText(tokenString)) {
            return tokenString;
        }

        logger.error("!!!!!!! AuthService.getRefreshToken() - tokenString is empty");
        throw new InvalidRefreshTokenException(logger);
    }

    /**
     * Redis 에서 refresh token 조회
     *
     * @param user 사용자 정보
     * @return String refresh token
     */
    private String getRefreshTokenInRedis(User user) {
        return redisService.getRefreshToken(user.getId());
    }

    /**
     * Redis 에서 access token 조회
     *
     * @param user 사용자 정보
     * @return String Access token
     */
    private String getAccessTokenInRedis(User user) {
        return redisService.getAccessToken(user.getId());
    }

    /**
     * Refresh token 변환
     *
     * @param token refresh token
     * @return 서버에서 생성한 토큰 객체
     */
    private WannabeToken parseRefreshToken(String token) {
        return jwtUtil.parseRefreshToken(token);
    }

    /**
     * 디바이스 토큰으로 사용자 조회
     *
     * @param userId      사용자 아이디
     * @param deviceToken 디바이스 토큰
     * @return User 사용자 정보
     */
    private User findUserByDeviceToken(long userId, String deviceToken) {
        return userService.findUserByDeviceToken(userId, deviceToken);
    }

    /**
     * 사용자 아이디로 디바이스 토큰 삭제
     *
     * @param user 사용자 정보
     */
    private void deleteUserDeviceToken(User user) {
        userService.deleteUserDeviceToken(user.getId());
    }

    private boolean hasText(String value) {
        return StringUtils.hasText(value);
    }
}
