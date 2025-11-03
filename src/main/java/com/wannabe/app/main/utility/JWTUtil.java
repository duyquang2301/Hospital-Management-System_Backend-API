package com.wannabe.app.main.utility;

import com.wannabe.app.main.data.dto.auth.WannabeToken;
import com.wannabe.app.main.exception.auth.ExpiredRefreshTokenException;
import com.wannabe.app.main.exception.auth.ExpiredTokenException;
import com.wannabe.app.main.exception.auth.InvalidTokenException;
import com.wannabe.app.main.utility.constant.TokenKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class JWTUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-token-expires-in-sec}")
    private int accessTokenExpires;
    @Value("${jwt.refresh-token-expires-in-sec}")
    private int refreshTokenExpires;
    private String APPLE_CLIENT_ID;

    public Claims getClaims(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    }

    public WannabeToken parseWannabeToken(String tokenString) {
        if (!StringUtil.hasText(tokenString)) {
            return null;
        }

        try {
            String token = removePrefix(tokenString);
            log.info("!!!!!!! JWTUtil.parseWannabeToken() - token: {}", token);
            Claims claims = getClaims(token);
            WannabeToken wannabeToken = JsonUtil.mapToObject(claims, WannabeToken.class);
            wannabeToken.updateExpirationTime(claims.getExpiration());
            wannabeToken.updateTokenString(token);
            return wannabeToken;
        } catch (MalformedJwtException e) {
            log.error("!!!!!!! JWTUtil.parseWannabeToken() - token is invalid MalformedJwtException, token : {}", tokenString);
            throw new InvalidTokenException(log);
        } catch (ExpiredJwtException e) {
            log.error("!!!!!!! JWTUtil.parseWannabeToken() - token is expired");
            throw new ExpiredTokenException(log);
        }
    }

    public WannabeToken parseRefreshToken(String tokenString) {
        if (tokenString == null) {
            return null;
        }

        try {
            String token = removePrefix(tokenString);
            Claims claims = getClaims(token);
            WannabeToken wannabeToken = JsonUtil.mapToObject(claims, WannabeToken.class);
            wannabeToken.updateExpirationTime(claims.getExpiration());
            wannabeToken.updateTokenString(token);
            return wannabeToken;
        } catch (ExpiredJwtException e) {
            log.error("!!!!!!! JWTUtil.parseRefreshToken() - token is expired");
            throw new ExpiredRefreshTokenException(log);
        }
    }

    public String generateAccessToken(long userId) {
        return createToken(userId, accessTokenExpires);
    }

    public String generateRefreshToken(long userId) {
        return createToken(userId, refreshTokenExpires);
    }

    private String createToken(long userId, long expiration) {
        Date now = new Date();
        Claims claims = Jwts.claims();
        claims.put(TokenKey.ATTR_KEY_USER_ID, userId);
        claims.put(TokenKey.ATTR_KEY_DATE_CREATED, now);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + (1000 * expiration)))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    private String removePrefix(String token) {
        if (token.startsWith(TokenKey.PREFIX_BEARER)) {
            return token.substring(7);
        }

        return token;
    }
}
