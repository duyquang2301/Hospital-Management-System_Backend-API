package com.wannabe.app.main.exception.paramter;

import com.wannabe.app.main.response.ResponseCode;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidLoginParameterException extends InvalidParameterException {

    public InvalidLoginParameterException(Logger l, boolean isLoginTypeError) {
        super(l);
        errorCode = isLoginTypeError ? ResponseCode.INVALID_LOGIN_TYPE : ResponseCode.INVALID_LOGIN_KEY;
    }
}
