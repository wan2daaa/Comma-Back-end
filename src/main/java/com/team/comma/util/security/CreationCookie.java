package com.team.comma.util.security;

import jakarta.servlet.http.Cookie;

public class CreationCookie {

	public static Cookie createRefreshToken(String refreshToken) {
		Cookie cookie = new Cookie("refreshToken", refreshToken);
		cookie.setDomain("localhost");
		cookie.setPath("/");
		cookie.setMaxAge(14 * 24 * 60 * 60 * 1000); // 14주간 저장
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		
		return cookie;
	}
	
	public static Cookie createAccessToken(String accessToken) {
		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setDomain("localhost");
		cookie.setPath("/");
		cookie.setMaxAge(14 * 24 * 60 * 60 * 1000); // 30분간 저장
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		
		return cookie;
	}
	
}