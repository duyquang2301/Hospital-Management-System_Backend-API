package com.wannabe.app.main.exception.auth;

import com.wannabe.app.main.response.ResponseCode;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class ExpiredRefreshTokenException extends AuthException {

    public ExpiredRefreshTokenException(Logger l) {
        super(l);
        errorCode = ResponseCode.EXPIRED_REFRESH_TOKEN;
    }
}
