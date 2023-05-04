package com.team.comma.util.jwt.support;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        Cookie cookies[] = ((HttpServletRequest) request).getCookies();

        if (cookies != null && cookies.length != 0) {
            String token = Arrays.stream(cookies) // 쿠키에서 AccessToken 추출
                .filter(c -> c.getName().equals("accessToken")).findFirst().map(Cookie::getValue)
                .orElse(null);

            if (token != null && jwtTokenProvider.validateToken(token)) { // 유효한 토큰?
                Authentication authentication = jwtTokenProvider.getAuthentication(
                    token); // 유효한 토큰의 정보를 가져옴
                SecurityContextHolder.getContext()
                    .setAuthentication(authentication); // SecurityContext 에 Authentication 객체를 저장
            }
        }

        chain.doFilter(request, response);
    }

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
}