package com.team.comma.service;

import javax.security.auth.login.AccountException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.OAuthRequest;
import com.team.comma.oauth.RegisterationOAuthUser;
import com.team.comma.oauth.IssuanceAccessToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {
	
	final private RegisterationOAuthUser createOAuthUser;
	final private IssuanceAccessToken getAccessToken;
	
	public MessageResponse loginOAuthServer(OAuthRequest oauthRequest) throws AccountException {
		if(oauthRequest.getType().equals("google")) {
			try {
				JsonNode json = getAccessToken.getGoogleAccessToken(oauthRequest.getCode());
				return createOAuthUser.createGoogleUser(json.get("access_token").toString());
			} catch (NullPointerException e) {
				throw new AccountException("유효하지 않은 접근입니다.");
			}
		}
		else if(oauthRequest.getType().equals("kakao")) {
			try {
				JsonNode json = getAccessToken.getKakaoAccessToken(oauthRequest.getCode());
				return createOAuthUser.createKakaoUser(json.get("access_token").toString());
			} catch (NullPointerException e) {
				throw new AccountException("유효하지 않은 접근입니다.");
			}
		}
		else if(oauthRequest.getType().equals("naver")) {
			try {
				JsonNode json = getAccessToken.getNaverAccessToken(oauthRequest.getCode() , oauthRequest.getState());
				return createOAuthUser.createNaverUser(json.get("access_token").toString());
			} catch (NullPointerException e) {
				throw new AccountException("유효하지 않은 접근입니다.");
			}
		}
		
		return MessageResponse.builder().code(-1).message("잘못된 소셜서버입니다.").build();
	}

}
