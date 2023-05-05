package com.team.comma.util.exception.handler;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.common.constant.ResponseCode;
import com.team.comma.util.jwt.exception.TokenForgeryException;
import com.team.comma.spotify.search.exception.TokenExpirationException;
import com.team.comma.spotify.search.exception.SpotifyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;


@RestControllerAdvice
public class GeneralExceptionHandler {

    /*
     * 토큰 변조 , 사용자를 찾을 수 없을 때 , 사용자가 이미 존재하거나 정보가 일치하지 않을 때
     */
    @ExceptionHandler({UsernameNotFoundException.class, AccountException.class})
    public ResponseEntity<MessageResponse> handleBadRequest(Exception e) {
        MessageResponse message = MessageResponse.of(ResponseCode.SIMPLE_REQUEST_FAILURE,
            e.getMessage());

        return ResponseEntity.badRequest().body(message);
    }

    /*
        토큰 변조
     */
    @ExceptionHandler(TokenForgeryException.class)
    public ResponseEntity<MessageResponse> handleForbiddenRequest(Exception e) {
        MessageResponse message = MessageResponse.of(ResponseCode.AUTHORIZATION_ERROR,
            e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }

    /*
     * OAuth2.0 존재하지 않은 이메일
     */
    @ExceptionHandler({AccountNotFoundException.class})
    public ResponseEntity<MessageResponse> handleAccountException(Exception e) {
        MessageResponse message = MessageResponse.of(ResponseCode.OAUTH_NO_EXISTENT_EMAIL,
            e.getMessage());

        return ResponseEntity.badRequest().body(message);
    }

    /*
     *  Spotify 예외
     */
    @ExceptionHandler({SpotifyException.class, UnauthorizedException.class})
    public ResponseEntity<MessageResponse> handleSpotifyException(Exception e) {
        MessageResponse message = MessageResponse.of(ResponseCode.SPOTIFY_FAILURE, e.getMessage());

        return ResponseEntity.internalServerError().body(message);
    }

    /*
        RefreshToken 만료
     */
    @ExceptionHandler({TokenExpirationException.class})
    public ResponseEntity<MessageResponse> handleExpireTokenException(Exception e) {
        MessageResponse message = MessageResponse.of(ResponseCode.REFRESH_TOKEN_EXPIRED,
            e.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
}