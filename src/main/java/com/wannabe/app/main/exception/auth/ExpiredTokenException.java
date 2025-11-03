package com.wannabe.app.main.exception.auth;

import com.wannabe.app.main.response.ResponseCode;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class ExpiredTokenException extends AuthException {

    public ExpiredTokenException(Logger l) {
        super(l);
        errorCode = ResponseCode.EXPIRED_AUTH_TOKEN;
    }
}
