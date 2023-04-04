package com.team.comma.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청")
public class RegisterRequest {
	@Schema(description = "사용자 이메일 값" , example = "example@naver.com")
	private String email;
	@Schema(description = "사용자 비밀번호 값" , example = "password")
	private String password;
	@Schema(description = "이름 정보")
	private String name;
	@Schema(description = "성별" , example = "남성 혹은 여성")
	private String sex;
	@Schema(description = "나이")
	private String age;
	@Schema(description = "음악 추천 날짜 / '2000-01-01 12:00:00' 형식으로 지정" , example = "2000-01-01 12:00:00")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime recommandTime;
	@Schema(description = "탈퇴 여부를 말하며 {1 : 탈퇴한 사용자} , {0 : 정상 사용자} 를 의미합니다. " , example = "0 혹은 1")
	private int isLeave;
}
