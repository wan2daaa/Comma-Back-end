package com.team.comma.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OAuth 사용자 요청 DTO")
public class OauthRequest {
	@Schema(description = "소셜 서버 타입", nullable = false, example = "kakao , google , naver 3개 중 알맞은 서버를 입력해주세요.")
	String type;
	@Schema(description = "소셜 서버에서 반환 된 code 쿼리 스트링", nullable = true)
	String code;
	@Schema(description = "소셜 서버에서 반환 된 state 쿼리 스트링", nullable = true)
	String state;
}
