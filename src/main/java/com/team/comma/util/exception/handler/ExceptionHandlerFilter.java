package com.team.comma.util.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.common.constant.ResponseCode;
import com.team.comma.util.jwt.exception.TokenForgeryException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (TokenForgeryException e) {
            setErrorResponse(response, ResponseCode.AUTHORIZATION_ERROR, e.getMessage());
        }
    }

    private void setErrorResponse(HttpServletResponse response, int errorCode,
        String errorMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        MessageResponse errorResponse = MessageResponse.of(errorCode, errorMessage);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
