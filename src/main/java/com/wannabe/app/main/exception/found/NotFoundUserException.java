package com.wannabe.app.main.exception.found;

import com.wannabe.app.main.response.ResponseCode;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundUserException extends NotFoundException {

    public NotFoundUserException(Logger l) {
        super(l);
        errorCode = ResponseCode.NOT_FOUND_USER;
    }

    public NotFoundUserException(Logger l, @Nullable String message) {
        super(l);
        errorCode = ResponseCode.NOT_FOUND_USER;
        this.additionalMessage = message;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
