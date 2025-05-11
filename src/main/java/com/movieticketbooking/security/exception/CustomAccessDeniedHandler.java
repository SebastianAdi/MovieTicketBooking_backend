package com.movieticketbooking.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticketbooking.payload.AuthenticationExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        AuthenticationExceptionResponse exceptionResponse = new AuthenticationExceptionResponse(
                LocalDateTime.now().toString(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                (accessDeniedException != null && accessDeniedException.getMessage() != null) ? accessDeniedException.getMessage() : "Authorization Failed",
                request.getRequestURI());
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(exceptionResponse));
    }
}
