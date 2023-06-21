package com.team.comma.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCodeEnum {
    REQUEST_SUCCESS("요청이 성공적으로 수행되었습니다.", 1),
    REQUEST_TYPE_MISMATCH("요청 타입이 잘못되었습니다.", -1),
    REQUEST_ENTITY_NOT_FOUND("해당 엔티티가 존재하지 않습니다.", -1),
    SIMPLE_REQUEST_FAILURE("요청을 처리하던 중 오류가 발생했습니다." , -1),
    OAUTH_NO_EXISTENT_EMAIL("이메일을 찾을 수 없습니다." , -3),
    AUTHENTICATION_ERROR("인증되지 않은 사용자입니다." , -4),
    AUTHORIZATION_ERROR("인가되지 않은 사용자입니다." , -4),
    REFRESH_TOKEN_EXPIRED("RefreshToken이 만료되었습니다." , -7),
    SPOTIFY_FAILURE("Spotify API 에서 예외가 발생했습니다." , -2),
    LOGIN_SUCCESS("로그인이 성공적으로 되었습니다." , 1) ,
    REGISTER_SUCCESS("성공적으로 가입되었습니다." , 1) ,
    ACCESS_TOKEN_CREATE("AccessToken이 재발급되었습니다." , 7) ,
    LOGOUT_SUCCESS("로그아웃이 성공적으로 되었습니다." , 1),
    PLAYLIST_ALARM_UPDATED("알람 설정이 변경되었습니다.", 2),
    PLAYLIST_NOT_FOUND("플레이리스트를 찾을 수 없습니다.", -5),
    PLAYLIST_DELETED("플레이리스트가 삭제되었습니다.", 2);

    private final String message;
    private final int code;
}