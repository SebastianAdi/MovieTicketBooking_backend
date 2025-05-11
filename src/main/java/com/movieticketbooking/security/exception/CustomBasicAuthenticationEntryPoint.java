package com.movieticketbooking.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieticketbooking.payload.AuthenticationExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        AuthenticationExceptionResponse exceptionResponse = new AuthenticationExceptionResponse(
                LocalDateTime.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                (authException != null && authException.getMessage() != null) ? authException.getMessage() : "Unauthorized",
                request.getRequestURI());
        response.getWriter().write(new ObjectMapper().writeValueAsString(exceptionResponse));

    }
}
