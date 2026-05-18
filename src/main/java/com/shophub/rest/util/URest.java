package com.shophub.rest.util;

import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.entity.rest.RestApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class URest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static class Security {

        static void handleAccessDenied(HttpServletRequest req, HttpServletResponse res, AccessDeniedException exc) throws IOException {
            var errCode = ErrorCodes.FORBIDDEN_USER;
            String jsonRes = objectMapper.writeValueAsString(RestApiResponse.fromErr(errCode));
            res.getWriter().write(jsonRes);
            res.setStatus(errCode.getHttpStatus().value());
            res.setContentType("application/json");
            res.getWriter().flush();
            // logging
        }
    }
}
