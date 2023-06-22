package com.team.comma.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Schema(description = "로그인 요청")
public class LoginRequest {

    @Schema(description = "사용자 이메일 값", example = "example@naver.com")
    private String email;
    @Schema(description = "사용자 비밀번호 값", example = "password")
    private String password;
}
