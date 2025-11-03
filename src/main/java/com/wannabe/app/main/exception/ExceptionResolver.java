package com.wannabe.app.main.exception;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.wannabe.app.main.exception.argument.IllegalArgumentException;
import com.wannabe.app.main.exception.auth.AuthException;
import com.wannabe.app.main.exception.found.NotFoundException;
import com.wannabe.app.main.exception.paramter.InvalidParameterException;
import com.wannabe.app.main.response.ErrorResponse;
import com.wannabe.app.main.utility.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionResolver {

    Logger logger = LogManager.getLogger(this.getClass());

    @ExceptionHandler({
        AuthException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse authExceptionHandler(HttpServletRequest request, Exception exception) {
        ErrorResponse response = null;
        ExceptionBase e;
        if (exception instanceof ExceptionBase) {
            e = (ExceptionBase) exception;
        } else {
            e = new AuthException(logger);
            LogUtils.warnLog(logger, request, exception);
        }
        response = new ErrorResponse(e);
        LogUtils.errorLog(e.logger, request, e);
        return response;
    }

    @ExceptionHandler({
        InvalidParameterException.class,
        MissingServletRequestParameterException.class,
        MissingRequestHeaderException.class,
        HttpMessageNotReadableException.class,
        MismatchedInputException.class,
        UnexpectedTypeException.class,
        MethodArgumentTypeMismatchException.class,
        BindException.class,
        ConstraintViolationException.class,
        ServletRequestBindingException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse invalidParameterExceptionHandler(HttpServletRequest request, Exception exception) {
        ErrorResponse response = null;
        ExceptionBase e;
        if (exception instanceof ExceptionBase) {
            e = (ExceptionBase) exception;
        } else {
            e = new InvalidParameterException(logger, exception.getClass().getSimpleName());
        }
        response = new ErrorResponse(e);
        LogUtils.errorLog(e.logger, request, e);
        return response;
    }

    @ExceptionHandler({
        NotFoundException.class,
        NoHandlerFoundException.class,
        HttpMediaTypeNotAcceptableException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFoundExceptionHandler(HttpServletRequest request, Exception exception) {
        ErrorResponse response = null;
        ExceptionBase e;
        if (exception instanceof ExceptionBase) {
            e = (ExceptionBase) exception;
        } else {
            e = new NotFoundException(logger, exception.getClass().getSimpleName());
        }
        response = new ErrorResponse(e);
        LogUtils.errorLog(e.logger, request, e);
        return response;
    }

    @ExceptionHandler({
        PermissionDeniedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse permissionDeniedException(HttpServletRequest request, Exception exception) {
        ErrorResponse response = null;
        ExceptionBase e;
        if (exception instanceof ExceptionBase) {
            e = (ExceptionBase) exception;
        } else {
            e = new PermissionDeniedException(logger, exception.getClass().getSimpleName());
        }
        response = new ErrorResponse(e);
        LogUtils.errorLog(e.logger, request, e);
        return response;
    }

    @ExceptionHandler({
        NotAllowedMethodException.class,
        HttpRequestMethodNotSupportedException.class
    })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public ErrorResponse notAllowedMethodExceptionHandler(HttpServletRequest request, Exception exception) {
        ErrorResponse response = null;
        ExceptionBase e;
        if (exception instanceof ExceptionBase) {
            e = (ExceptionBase) exception;
        } else {
            e = new NotAllowedMethodException(logger, exception.getClass().getSimpleName());
        }
        response = new ErrorResponse(e);
        LogUtils.errorLog(e.logger, request, e);
        return response;
    }

    @ExceptionHandler({
        ServiceUnavailableException.class
    })
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ErrorResponse unavailableServiceExceptionHandler(HttpServletRequest request, Exception exception) {
        ErrorResponse response = null;
        ExceptionBase e;
        if (exception instanceof ExceptionBase) {
            e = (ExceptionBase) exception;
        } else {
            e = new ServiceUnavailableException(logger, exception.getClass().getSimpleName());
        }
        response = new ErrorResponse(e);
        LogUtils.errorLog(e.logger, request, e);
        return response;
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ErrorResponse handleException(HttpServletRequest request, MethodArgumentNotValidException exception) {
        ErrorResponse response = null;
        ExceptionBase e;
        e = new InvalidParameterException(logger, Objects.requireNonNull(exception.getFieldError()).getDefaultMessage());
        response = new ErrorResponse(e);
        LogUtils.errorLog(e.logger, request, e);
        return response;
    }

    @ExceptionHandler({
        IllegalArgumentException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse IllegalArgumentException(HttpServletRequest request, Exception exception) {
        ErrorResponse response = null;
        ExceptionBase e;
        if (exception instanceof ExceptionBase) {
            e = (ExceptionBase) exception;
        } else {
            e = new IllegalArgumentException(logger, exception.getClass().getSimpleName());
        }
        response = new ErrorResponse(e);
        LogUtils.errorLog(e.logger, request, e);
        return response;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse unknownErrorHandler(HttpServletRequest request, Exception e) {
        ErrorResponse response = new ErrorResponse();
        logger.error(e.getMessage(), e);
        LogUtils.errorLog(logger, request, e);
        return response;
    }
}
