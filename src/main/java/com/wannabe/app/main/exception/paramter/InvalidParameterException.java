package com.wannabe.app.main.exception.paramter;

import com.wannabe.app.main.exception.ExceptionBase;
import com.wannabe.app.main.response.ResponseCode;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidParameterException extends ExceptionBase {

    public InvalidParameterException(Logger l) {
        logger = l;
        errorCode = ResponseCode.INVALID_PARAMETER;
    }

    public InvalidParameterException(Logger l, @Nullable String message) {
        logger = l;
        errorCode = ResponseCode.INVALID_PARAMETER;
        this.additionalMessage = message;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
