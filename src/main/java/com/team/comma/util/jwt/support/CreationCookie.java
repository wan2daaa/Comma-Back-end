package com.team.comma.util.jwt.support;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CreationCookie {

    @Value("${spring.server}")
    private static String DOMAIN_URL;

    public static ResponseCookie createResponseAccessToken(String cookieName) {
        return ResponseCookie.from("accessToken", cookieName)
            .httpOnly(true)
            //.secure(true)
            .path("/")
            //.sameSite("None")
            .maxAge(30 * 60 * 1000L)
            .domain(DOMAIN_URL)
            .build();
    }

    public static ResponseCookie createResponseRefreshToken(String cookieName) {
        return ResponseCookie.from("refreshToken", cookieName)
            .httpOnly(true)
            //.secure(true)
            .path("/")
            //.sameSite("None")
            .maxAge(14 * 24 * 60 * 60 * 1000L)
            .domain(DOMAIN_URL)
            .build();
    }

}
