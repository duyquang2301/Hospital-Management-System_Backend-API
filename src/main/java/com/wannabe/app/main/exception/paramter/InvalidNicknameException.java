package com.wannabe.app.main.exception.paramter;

import com.wannabe.app.main.response.ResponseCode;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidNicknameException extends InvalidParameterException {

    public InvalidNicknameException(Logger l) {
        super(l);
        errorCode = ResponseCode.INVALID_NAME;
    }
}
