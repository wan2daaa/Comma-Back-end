package com.team.comma.util.jwt.support;

import org.springframework.http.ResponseCookie;

public class CreationCookie {

    final private static String domainUrl = "localhost";

    public static ResponseCookie createResponseAccessToken(String cookieName) {
        return ResponseCookie.from("accessToken", cookieName)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(30 * 60 * 1000)
                .domain(domainUrl)
                .build();
    }

    public static ResponseCookie createResponseRefreshToken(String cookieName) {
        return ResponseCookie.from("refreshToken", cookieName)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(14 * 24 * 60 * 60 * 1000)
                .domain(domainUrl)
                .build();
    }

}
