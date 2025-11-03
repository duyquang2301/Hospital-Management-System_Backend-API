package com.wannabe.app.main.exception.sql;

import com.wannabe.app.main.exception.ExceptionBase;
import com.wannabe.app.main.response.ResponseCode;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseException extends ExceptionBase {

    public DatabaseException(Logger l) {
        logger = l;
        errorCode = ResponseCode.DATABASE_ERROR;
    }

    public DatabaseException(Logger l, @Nullable String message) {
        logger = l;
        errorCode = ResponseCode.DATABASE_ERROR;
        this.additionalMessage = message;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
