package com.team.comma.util.security;

import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;

public class CreationCookie {

    final private static String domainUrl = "localhost";
    public static ResponseCookie createResponseAccessToken(String cookieName) {
        return ResponseCookie.from("accessToken" , cookieName)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(30 * 60 * 1000)
                .domain(domainUrl)
                .build();
    }

    public static ResponseCookie createResponseRefreshToken(String cookieName) {
        return ResponseCookie.from("refreshToken" , cookieName)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(14 * 24 * 60 * 60 * 1000)
                .domain(domainUrl)
                .build();
    }

    public static Cookie createAccessToken(String cookieName) {
        Cookie cookie = new Cookie("accessToken" , cookieName);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60 * 1000);
        cookie.setDomain(domainUrl);

        return cookie;
    }

    public static Cookie createRefreshToken(String cookieName) {
        Cookie cookie = new Cookie("refreshToken" , cookieName);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(14 * 24 * 60 * 60 * 1000);
        cookie.setDomain(domainUrl);

        return cookie;
    }

}
