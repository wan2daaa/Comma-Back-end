package com.team.comma.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청")
public class LoginRequest {

    @Schema(description = "사용자 이메일 값", example = "example@naver.com")
    private String email;
    @Schema(description = "사용자 비밀번호 값", example = "password")
    private String password;
}
