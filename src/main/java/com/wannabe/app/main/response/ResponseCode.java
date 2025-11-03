package com.wannabe.app.main.response;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ResponseCode {
    SUCCESS(0),

    // HTTP_CODE 400
    // InvalidParameterException
    INVALID_PARAMETER(1000),
    INVALID_LOGIN_TYPE(1001),
    INVALID_LOGIN_KEY(1002),
    ALREADY_EXIST_USER(1003),
    INVALID_LOCATION(1004),
    INVALID_SURGERY_PART(1005),
    INVALID_GENDER(1006),
    INVALID_NAME(1007),
    INVALID_NICKNAME(1008),
    INVALID_DEVICE_TOKEN(1009),
    ALREADY_BOOKMARKED(1010),
    INVALID_PHONE_NUMBER(1011),
    INVALID_CHAT(1012),
    INVALID_BLOCK(1013),
    ALREADY_BLOCKED(1014),
    BLOCKED_USER(1015),
    NOT_ACTIVE_USER(1016),
    INVALID_COUNSEL(1017),
    INVALID_VIRTUAL_SURGERY(1018),
    INVALID_FILE_FORMAT(1019),
    INVALID_BANNED_USER(1020),
    ALREADY_UNBLOCKED(1021),

    // HTTP_CODE 401
    // AuthException
    NO_AUTH_TOKEN(2001),
    INVALID_AUTH_TOKEN(2002),
    EXPIRED_AUTH_TOKEN(2003),
    INVALID_REFRESH_TOKEN(2004),
    EXPIRED_REFRESH_TOKEN(2005),
    UNKNOWN_AUTH_ERROR(2000),

    // HTTP_CODE 403
    // PermissionDeniedException
    NOT_ALLOWED(3000),

    // HTTP_CODE 404
    // NotFoundException
    NOT_FOUND(4000),
    NOT_FOUND_USER(4001),
    NOT_FOUND_HOSPITAL(4002),
    NOT_FOUND_VIRTUAL_SURGERY(4003),
    NOT_FOUND_EVENT(4004),
    NOT_FOUND_BOOKMARK(4005),
    NOT_FOUND_PROMOTION(4006),
    NOT_FOUND_COUNSEL(4007),
    // HTTP_CODE 405
    // UnavailableException
    METHOD_NOT_ALLOWED(5000),

    // HTTP_CODE 503
    SERVICE_UNAVAIABLE(6000),

    // HTTP_CODE 500
    DATABASE_ERROR(9000),
    WEB_CLIENT_ERROR(9001),
    UNKNOWN_ERROR(-1);

    private int code;

    private static final Map<Integer, ResponseCode> findByProfile =
        Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(ResponseCode::getCode, Function.identity())));

    ResponseCode(int c) {
        this.code = c;
    }

    public int getCode() {
        return this.code;
    }

    public static ResponseCode valueOf(int code) {
        return Optional.ofNullable(findByProfile.get(code)).orElse(UNKNOWN_ERROR);
    }

    public String toString() {
        return switch (this) {
            case SUCCESS -> "ok";
            case INVALID_PARAMETER -> "Invalid Parameter";
            case INVALID_LOGIN_TYPE -> "Invalid Login Type";
            case INVALID_LOGIN_KEY -> "Invalid Login Key";
            case NO_AUTH_TOKEN -> "No idToken";
            case INVALID_AUTH_TOKEN -> "Invalid  Auth Token";
            case EXPIRED_AUTH_TOKEN -> "Expired Auth Token";
            case INVALID_REFRESH_TOKEN -> "Invalid refresh Token";
            case EXPIRED_REFRESH_TOKEN -> "Expired refresh Token";
            case UNKNOWN_AUTH_ERROR -> "Unknown Auth Error";
            case ALREADY_EXIST_USER -> "Already Exist User";
            case INVALID_LOCATION -> "Invalid Location";
            case INVALID_SURGERY_PART -> "Invalid Surgery Part";
            case INVALID_GENDER -> "Invalid Gender";
            case INVALID_NAME -> "Invalid Name";
            case INVALID_NICKNAME -> "Invalid Nickname";
            case INVALID_DEVICE_TOKEN -> "Invalid Device Token";
            case ALREADY_BOOKMARKED -> "Already Bookmarked";
            case INVALID_PHONE_NUMBER -> "Invalid Phone Number";
            case INVALID_CHAT -> "Invalid Chat";
            case INVALID_BLOCK -> "Invalid Block";
            case INVALID_BANNED_USER -> "Invalid Banned User";
            case ALREADY_BLOCKED -> "Already Blocked";
            case BLOCKED_USER -> "Blocked User";
            case NOT_ACTIVE_USER -> "Not Active User";
            case INVALID_COUNSEL -> "Invalid Counsel";
            case INVALID_VIRTUAL_SURGERY -> "Invalid Virtual Surgery";
            case INVALID_FILE_FORMAT -> "Invalid File Format";
            case ALREADY_UNBLOCKED -> "Already Unblocked";
            case NOT_ALLOWED -> "Not Allowed";
            case NOT_FOUND -> "Not Found";
            case NOT_FOUND_USER -> "Not Found User";
            case NOT_FOUND_HOSPITAL -> "Not Found Hospital";
            case NOT_FOUND_VIRTUAL_SURGERY -> "Not Found Virtual Surgery";
            case NOT_FOUND_EVENT -> "Not Found Event";
            case NOT_FOUND_BOOKMARK -> "Not Found Bookmark";
            case NOT_FOUND_PROMOTION -> "Not Found Promotion";
            case NOT_FOUND_COUNSEL -> "Not Found Counsel";
            case METHOD_NOT_ALLOWED -> "Not Allowed Method";
            case SERVICE_UNAVAIABLE -> "Temporary Service Unavailable";
            case UNKNOWN_ERROR -> "Unknown Error";
            default -> "Unhandled error";
        };
    }
}
