package com.wannabe.app.main.exception.auth;

import com.wannabe.app.main.response.ResponseCode;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends AuthException {

    public InvalidTokenException(Logger l) {
        super(l);
        errorCode = ResponseCode.INVALID_AUTH_TOKEN;
    }
}
