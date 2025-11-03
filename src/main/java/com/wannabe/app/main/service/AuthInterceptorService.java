package com.wannabe.app.main.service;

import com.wannabe.app.main.annotation.AdminCallable;
import com.wannabe.app.main.annotation.AnonymousCallable;
import com.wannabe.app.main.annotation.RefreshCallable;
import com.wannabe.app.main.data.dto.auth.WannabeToken;
import com.wannabe.app.main.data.entity.Admin;
import com.wannabe.app.main.data.entity.User;
import com.wannabe.app.main.exception.auth.InvalidTokenException;
import com.wannabe.app.main.exception.paramter.InvalidBannedUserException;
import com.wannabe.app.main.mapper.AdminMapper;
import com.wannabe.app.main.mapper.UserMapper;
import com.wannabe.app.main.utility.JWTUtil;
import com.wannabe.app.main.utility.constant.HeaderKey;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthInterceptorService {

    private final JWTUtil jwtUtil;
    private final RedisService redisService;
    private final AdminMapper adminMapper;
    private final UserMapper userMapper;

    Logger logger = LogManager.getLogger(this.getClass());

    public boolean isAnonymousCallable(HandlerMethod handlerMethod) {
        return handlerMethod.getMethodAnnotation(AnonymousCallable.class) != null;
    }

    public boolean isAdminCallable(HandlerMethod handlerMethod) {
        return handlerMethod.getMethodAnnotation(AdminCallable.class) != null;
    }

    public boolean isRefreshCallable(HandlerMethod handlerMethod) {
        return handlerMethod.getMethodAnnotation(RefreshCallable.class) != null;
    }

    /**
     * 토큰 검증
     *
     * @param request Client 요청 정보
     */
    public void verifyToken(HttpServletRequest request) {
        WannabeToken wannabeToken = jwtUtil.parseWannabeToken(getAccessToken(request));
        validateParseToken(wannabeToken);
        validateRedisToken(wannabeToken);
        validateActiveUser(wannabeToken.getUserId());

        setAttributeUserId(request, wannabeToken);
    }

    /**
     * 관리자 토큰 검증
     *
     * @param request Client 요청 정보
     */
    public void verifyAdminToken(HttpServletRequest request) {
        WannabeToken wannabeToken = jwtUtil.parseWannabeToken(getAccessToken(request));
        validateParseToken(wannabeToken);
        validateAdminRedisToken(wannabeToken);

        Admin findAdmin = adminMapper.findAdminById(wannabeToken.getUserId());

        if (findAdmin == null || !findAdmin.isActive()) {
            log.error("!!!!!! AuthInterceptorService.verifyAdminToken() - findAdmin is null or not active admin");
            throw new InvalidTokenException(logger);
        }
    }

    /**
     * refresh token 검증
     *
     * @param request Client 요청 정보
     */
    public void verifyRefreshToken(HttpServletRequest request) {
        WannabeToken wannabeToken = jwtUtil.parseRefreshToken(getAccessToken(request));
        validateParseToken(wannabeToken);

        setAttributeUserId(request, wannabeToken);
    }

    /**
     * Client 요청 정보 에 속성 부여
     *
     * @param request      Client 요청 정보
     * @param wannabeToken 토큰 객체
     */
    private void setAttributeUserId(HttpServletRequest request, WannabeToken wannabeToken) {
        request.setAttribute(HeaderKey.USER_ID, wannabeToken.getUserId());
    }

    /**
     * 토큰 객체 검증
     *
     * @param wannabeToken 토큰 객체
     */
    private void validateParseToken(WannabeToken wannabeToken) {
        if (wannabeToken != null) {
            return;
        }

        log.error("!!!!!! AuthInterceptorService.validateParseToken() - wannabeToken is null");
        throw new InvalidTokenException(logger);
    }

    /**
     * redis token 검증
     *
     * @param wannabeToken 토큰 객체
     */
    private void validateRedisToken(WannabeToken wannabeToken) {
        String redisToken = getAccessTokenInRedis(wannabeToken.getUserId());

        System.out.println("!!!!!! validateRedisToken() - redisToken: " + redisToken);

        if (isValidRedisToken(redisToken, wannabeToken)) {
            return;
        }

        log.error("!!!!!! AuthInterceptorService.validateRedisToken() - redisToken is invalid");
        throw new InvalidTokenException(logger);
    }

    /**
     * redis 관리자 token 검증
     *
     * @param wannabeToken 토큰 객체
     */
    private void validateAdminRedisToken(WannabeToken wannabeToken) {
        String redisToken = getAdminAccessTokenInRedis(wannabeToken.getUserId());

        System.out.println("!!!!!! validateRedisToken() - redisToken: " + redisToken);

        if (isValidRedisToken(redisToken, wannabeToken)) {
            return;
        }

        log.error("!!!!!! AuthInterceptorService.validateRedisToken() - redisToken is invalid");
        throw new InvalidTokenException(logger);
    }

    /**
     * redis token 검증
     *
     * @param redisToken   redis 토큰
     * @param wannabeToken 토큰 객체
     * @return redis token 검증 여부
     */
    private boolean isValidRedisToken(String redisToken, WannabeToken wannabeToken) {
        return hasText(redisToken) && redisToken.equals(wannabeToken.getToken());
    }

    /**
     * Client 요청 정보에서 token 조회
     *
     * @param request Client 요청 정보
     * @return String token
     */
    private String getAccessToken(HttpServletRequest request) {
        String tokenString = request.getHeader(HeaderKey.AUTHORIZATION);

        log.info("!!!!!!! AuthInterceptorService.getAccessToken() - tokenString: " + tokenString);

        if (hasText(tokenString)) {
            return tokenString;
        }

        logger.error("!!!!!!! AuthInterceptorService.getAccessToken() - tokenString is empty");
        throw new InvalidTokenException(logger);
    }

    private boolean hasText(String value) {
        return StringUtils.hasText(value);
    }

    /**
     * redis 에 있는 access token 조회
     *
     * @param userId 사용자 아이디
     * @return String access token
     */
    private String getAccessTokenInRedis(long userId) {
        return redisService.getAccessToken(userId);
    }

    /**
     * redis 에 있는 admin access token 조회
     *
     * @param userId 관리자 아이디
     * @return String access token
     */
    private String getAdminAccessTokenInRedis(long userId) {
        return redisService.getAdminAccessToken(userId);
    }

    /**
     * 토큰의 사용자가 유효한 사용자인지 검증
     *
     * @param userId 사용자 아이디
     */
    private void validateActiveUser(long userId) {
        User findUser = userMapper.findUserById(userId);
        if (findUser == null) {
            log.error("!!!!!! AuthInterceptorService.validateActiveUser() - user is not active");
            throw new InvalidTokenException(logger);
        }

        if (!findUser.isActiveUser()) {
            log.error("!!!!!! AuthInterceptorService.validateActiveUser() - user is not active");
            throw new InvalidTokenException(logger);
        }

        if (findUser.isBannedUser()) {
            log.error("!!!!!! AuthInterceptorService.validateActiveUser() - user is banned");
            throw new InvalidBannedUserException(logger);
        }
    }
}
