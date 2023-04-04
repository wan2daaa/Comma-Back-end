package com.team.comma.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.team.comma.dto.MessageDTO;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateOAuthUser {
	
	final private UserService userService;
	
	public MessageDTO createKakaoUser(String token) throws AccountException {
		String reqURL = "https://kapi.kakao.com/v2/user/me"; // access_token을 이용하여 사용자 정보 조회
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "Bearer " + token); // 전송할 header 작성, access_token전송

			// 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);

			// Gson 라이브러리로 JSON파싱
			JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
			String email = jsonObject.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
			
			return userService.loginOauth(createUser(email));
		} catch (IOException e) {
			e.printStackTrace();
			return MessageDTO.builder().code(-1).message("로그인을 하는 도중에 오류가 발생했습니다.").build();
		}
	}

	public MessageDTO createNaverUser(String token) throws AccountException {
		String reqURL = "https://openapi.naver.com/v1/nid/me"; // access_token을 이용하여 사용자 정보 조회
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "Bearer " + token); // 전송할 header 작성, access_token전송

			// 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);

			// Gson 라이브러리로 JSON파싱
			JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
			
			try {
				String email = jsonObject.getAsJsonObject().get("response").getAsJsonObject().get("email").getAsString();
				
				return userService.loginOauth(createUser(email));
			} catch (NullPointerException e) {
				throw new AccountNotFoundException("해당 계정의 이메일이 존재하지 않습니다.");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return MessageDTO.builder().code(-1).message("로그인을 하는 도중에 오류가 발생했습니다.").build();
		}
	}
	
	public MessageDTO createGoogleUser(String token) throws AccountException {
		String reqURL = "https://www.googleapis.com/userinfo/v2/me?access_token=" + token; // access_token을 이용하여 사용자 정보 조회
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setDoOutput(true);

			// 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);

			// Gson 라이브러리로 JSON파싱
			JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
			String email = jsonObject.getAsJsonObject().get("email").getAsString();
			
			return userService.loginOauth(createUser(email));
		} catch (IOException e) {
			e.printStackTrace();
			return MessageDTO.builder().code(-1).message("로그인을 하는 도중에 오류가 발생했습니다.").build();
		}
	}
	
	public RegisterRequest createUser(String email) {
		return RegisterRequest.builder()
				.email(email)
				.build();
	}
}
