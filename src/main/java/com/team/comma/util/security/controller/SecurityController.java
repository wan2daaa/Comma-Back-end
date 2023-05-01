package com.team.comma.util.security.controller;

import com.team.comma.util.jwt.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.common.dto.MessageResponse;

import lombok.RequiredArgsConstructor;

import static com.team.comma.common.constant.ResponseCode.AUTHORIZATION_ERROR;
import static com.team.comma.common.constant.ResponseCode.LOGOUT_SUCCESS;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    final private JwtService jwtService;

    @GetMapping(value = "/authentication/denied")
    public ResponseEntity<MessageResponse> informAuthenticationDenied(
        @CookieValue(name = "refreshToken", required = false) String authorization) {
        if (authorization == null) {
            MessageResponse message = MessageResponse.of(AUTHORIZATION_ERROR, "인증되지 않은 사용자입니다.");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
        }

        return jwtService.validateRefreshToken(authorization);
    }

    @GetMapping(value = "/authorization/denied")
    public ResponseEntity<MessageResponse> informAuthorizationDenied() {
        MessageResponse message = MessageResponse.of(AUTHORIZATION_ERROR, "인가되지 않은 사용자입니다.");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }


    @GetMapping("/security")
    public String getSecurity() {
        return "Security";
    }

    @GetMapping(value = "/logout/message")
    public ResponseEntity<MessageResponse> logoutMessage() {
        MessageResponse message = MessageResponse.of(LOGOUT_SUCCESS, "로그아웃이 성공적으로 되었습니다.");

        return ResponseEntity.ok().body(message);
    }

}
