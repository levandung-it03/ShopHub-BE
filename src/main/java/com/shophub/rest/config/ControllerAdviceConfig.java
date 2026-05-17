package com.shophub.rest.config;

import com.shophub.rest.config.exception.BaseRestException;
import com.shophub.rest.config.exception.RestServiceException;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.entity.rest.RestApiResponse;
import com.shophub.rest.util.UString;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ControllerAdviceConfig {

    /*===========================IMPLEMENTED LOG================================*/

    @ExceptionHandler(RestServiceException.class)
    public RestApiResponse<Void> handleServiceExc(RestServiceException e) {
        if (e.getErrorCode() != null)
            return RestApiResponse.fromErr(e.getErrorCode());
        else
            return new RestApiResponse<Void>(
            ErrorCodes.UNAWARE_ERR.getHttpStatus(),
            RestApiResponse.ResponseBody.<Void>builder()
                .code(e.getClass().getSimpleName())
                .msg(e.getMessage())
                .time(Instant.now())
                .build());
    }

    @ExceptionHandler(BaseRestException.class)
    public RestApiResponse<Void> handleUncaughtExc(BaseRestException e) {
        return this.mappingGeneralExcToResponse(e);
    }


    /*===========================HAVEN'T IMPLEMENTED LOG================================*/

    /**
     * INFO: Cause of uncatchable Exception from Hibernate Validator, or cannot configure Aspect with @Valid
     * or @Validated easily, this method helps us to log the error.
     */
    @ExceptionHandler(BindException.class)
    public RestApiResponse<Void> handleHibernateValidationExc(BindException e) {
        var response = new RestApiResponse<Void>(
            ErrorCodes.UNAWARE_ERR.getHttpStatus(),
            RestApiResponse.ResponseBody.<Void>builder()
                .code(e.getClass().getSimpleName())
                .msg(this.cuttingHibernateValidationExcMsg(e))
                .time(Instant.now())
                .build());

//        logService.servletRequestExcLogActivate(e, ErrorCodes.HIBERNATE_VALIDATION_ERR_LOG);

        return response;
    }

    private String cuttingHibernateValidationExcMsg(BindException e) {
        if (e instanceof MethodArgumentNotValidException castedExc) {
            return castedExc.getFieldError() == null
                ? castedExc.getMessage()
                : UString.SFormat
                .defaultPattern(ErrorCodes.HIBERNATE_VALIDATION_ERR_LOG.getMsg())
                .param(UString.castNullableString(castedExc.getFieldError().getCode()))
                .param(castedExc.getFieldError().getField())
                .param(UString.castNullableString(castedExc.getFieldError().getRejectedValue()))
                .ok();
        }
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public RestApiResponse<Void> handleUnawareExc(Exception e) {
        // Logging unaware exception (such as: Database connection lost,...)
//        logService.servletRequestExcLogActivate(e, ErrorCodes.UNAWARE_ERR);
        return this.mappingGeneralExcToResponse(e);
    }

    private RestApiResponse<Void> mappingGeneralExcToResponse(Exception e) {
        return new RestApiResponse<>(
            ErrorCodes.UNAWARE_ERR.getHttpStatus(),
            RestApiResponse.ResponseBody.<Void>builder()
                .code(e.getClass().getSimpleName())
                .msg(e.getMessage())
                .time(Instant.now())
                .build());
    }

}
