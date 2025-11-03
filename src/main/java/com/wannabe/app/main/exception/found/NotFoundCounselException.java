package com.wannabe.app.main.exception.found;

import com.wannabe.app.main.response.ResponseCode;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundCounselException extends NotFoundException {

    public NotFoundCounselException(Logger l) {
        super(l);
        errorCode = ResponseCode.NOT_FOUND_COUNSEL;
    }

    public NotFoundCounselException(Logger l, @Nullable String message) {
        super(l);
        errorCode = ResponseCode.NOT_FOUND_COUNSEL;
        this.additionalMessage = message;
    }
}
