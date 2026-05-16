package com.shophub.rest.config.filter;

//import com.fasterxml.jackson.databind.ObjectMapper;
import com.shophub.rest.config.CommonEnvConfig;
import com.shophub.rest.config.exception.FilterHandlingException;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.config.rest.RequestCtxDataDelivery;
import com.shophub.rest.entity.rest.RestApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public abstract class BaseFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    protected RequestCtxDataDelivery reqCtxDelivery;
    protected CommonEnvConfig env;

    protected BaseFilter(RequestCtxDataDelivery requestContext, CommonEnvConfig envConfig) {
        this.reqCtxDelivery = requestContext;
        this.env = envConfig;
    }

    protected void handleFilterException(HttpServletResponse response, Exception e) throws IOException {
        var isManuallyThrownExc = e instanceof FilterHandlingException;
        var errorEnum = isManuallyThrownExc
            ? ((FilterHandlingException) e).getErrorCode()
            : ErrorCodes.UNAWARE_ERR;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        var writer = response.getWriter();
        var json = objectMapper.writeValueAsString(
            new RestApiResponse<>(errorEnum.getHttpStatus(),
                RestApiResponse.ResponseBody.<Void>builder()
                    .code(errorEnum.toString())
                    .msg(isManuallyThrownExc ? errorEnum.getMsg() : e.getMessage())
                    .time(Instant.now())
                    .build()));

        writer.write(json);
        // logging
        writer.flush();
    }

}