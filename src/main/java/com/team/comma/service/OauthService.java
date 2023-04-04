package com.team.comma.service;

import javax.security.auth.login.AccountException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.team.comma.dto.MessageDTO;
import com.team.comma.dto.OauthRequest;
import com.team.comma.oauth.CreateOAuthUser;
import com.team.comma.oauth.GetAccessToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OauthService {
	
	final private CreateOAuthUser createOauthUser;
	final private GetAccessToken getAccessToken;
	
	public MessageDTO loginOauthServer(OauthRequest oauthRequest) throws AccountException {
		if(oauthRequest.getType().equals("google")) {
			try {
				JsonNode json = getAccessToken.getGoogleAccessToken(oauthRequest.getCode());
				return createOauthUser.createGoogleUser(json.get("access_token").toString());
			} catch (NullPointerException e) {
				System.out.println(e);
				throw new AccountException("유효하지 않은 접근입니다.");
			}
		}
		else if(oauthRequest.getType().equals("kakao")) {
			try {
				JsonNode json = getAccessToken.getKakaoAccessToken(oauthRequest.getCode());
				return createOauthUser.createKakaoUser(json.get("access_token").toString());
			} catch (NullPointerException e) {
				throw new AccountException("유효하지 않은 접근입니다.");
			}
		}
		else if(oauthRequest.getType().equals("naver")) {
			try {
				JsonNode json = getAccessToken.getNaverAccessToken(oauthRequest.getCode() , oauthRequest.getState());
				return createOauthUser.createNaverUser(json.get("access_token").toString());
			} catch (NullPointerException e) {
				throw new AccountException("유효하지 않은 접근입니다.");
			}
		}
		
		return MessageDTO.builder().code(-1).message("잘못된 소셜서버입니다.").build();
	}

}
