package com.wannabe.app.main.service;

import static com.wannabe.app.main.utility.constant.TokenKey.ACCESS_TOKEN_TYPE;
import static com.wannabe.app.main.utility.constant.TokenKey.REFRESH_TOKEN_TYPE;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${jwt.access-token-expires-in-sec}")
    private int accessTokenExpires;
    @Value("${jwt.refresh-token-expires-in-sec}")
    private int refreshTokenExpires;


    /**
     * access token 저장
     *
     * @param userId 사용자 아이디
     * @param token  access token 토큰
     */
    public void insertAccessToken(long userId, String token) {
        deleteAccessToken(userId);
        redisTemplate.opsForValue().set(makeAccessTokenKey(userId), token, accessTokenExpires, TimeUnit.SECONDS);
    }

    /**
     * refresh token 저장
     *
     * @param userId 사용자 아이디
     * @param token  refresh token 토큰
     */
    public void insertRefreshToken(long userId, String token) {
        deleteRefreshToken(userId);
        redisTemplate.opsForValue().set(makeRefreshTokenKey(userId), token, refreshTokenExpires, TimeUnit.SECONDS);
    }

    /**
     * access token 조회
     *
     * @param userId 사용자 아이디
     * @return String access token
     */
    public String getAccessToken(long userId) {
        return (String) redisTemplate.opsForValue().get(makeAccessTokenKey(userId));
    }

    /**
     * 관리자 access token 조회
     *
     * @param adminId 관리자 아이디
     * @return String access token
     */
    public String getAdminAccessToken(long adminId) {
        return (String) redisTemplate.opsForValue().get(makeAdminAccessTokenKey(adminId));
    }

    /**
     * refresh token 조회
     *
     * @param userId 사용자 아이디
     * @return String refresh token
     */
    public String getRefreshToken(long userId) {
        return (String) redisTemplate.opsForValue().get(makeRefreshTokenKey(userId));
    }

    /**
     * 토큰 만료
     *
     * @param userId 사용자 아이디
     */
    public void expireToken(long userId) {
        deleteAccessToken(userId);
        deleteRefreshToken(userId);
    }

    /**
     * access token 삭제
     *
     * @param userId 사용자 아이디
     */
    private void deleteAccessToken(long userId) {
        redisTemplate.delete(makeAccessTokenKey(userId));
    }

    /**
     * refresh token 삭제
     *
     * @param userId 사용자 아이디
     */
    private void deleteRefreshToken(long userId) {
        redisTemplate.delete(makeRefreshTokenKey(userId));
    }

    /**
     * access token key 생성
     *
     * @param userId 사용자 아아디
     * @return String access token key
     */
    private String makeAccessTokenKey(long userId) {
        return makeKey(ACCESS_TOKEN_TYPE, userId);
    }

    /**
     * Admin access token key 생성
     *
     * @param adminId 관리자 아이디
     * @return String Admin access token key
     */
    private String makeAdminAccessTokenKey(long adminId) {
        return makeAdminKey(ACCESS_TOKEN_TYPE, adminId);
    }

    /**
     * refresh token key 생성
     *
     * @param userId 사용자 아아디
     * @return String refresh token key
     */
    private String makeRefreshTokenKey(long userId) {
        return makeKey(REFRESH_TOKEN_TYPE, userId);
    }

    /**
     * 사용자 key 생성
     *
     * @param type   키 타입
     * @param userId 사용자 아이디
     * @return String key
     */
    private String makeKey(String type, long userId) {
        return type + ":" + userId;
    }

    /**
     * 관리자 key 생성
     *
     * @param type    키 타입
     * @param adminId 관리자 아이디
     * @return String key
     */
    private String makeAdminKey(String type, long adminId) {
        return "ADM-" + type + ":" + adminId;
    }
}
