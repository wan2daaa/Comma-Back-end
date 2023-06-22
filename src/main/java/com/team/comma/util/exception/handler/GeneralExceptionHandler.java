package com.team.comma.util.exception.handler;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.follow.exception.FollowingException;
import com.team.comma.spotify.favorite.artist.exception.FavoriteArtistException;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.search.exception.SpotifyException;
import com.team.comma.spotify.search.exception.TokenExpirationException;
import com.team.comma.util.auth.exception.OAuth2EmailNotFoundException;
import com.team.comma.util.jwt.exception.TokenForgeryException;
import com.team.comma.util.s3.exception.S3Exception;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;
import javax.security.auth.login.AccountException;

import static com.team.comma.common.constant.ResponseCodeEnum.*;


@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler({UsernameNotFoundException.class, AccountException.class , S3Exception.class
            , FollowingException.class , FavoriteArtistException.class })
    public ResponseEntity<MessageResponse> handleBadRequest(Exception e) {
        MessageResponse message = MessageResponse.of(SIMPLE_REQUEST_FAILURE.getCode() ,
            e.getMessage());

        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(TokenForgeryException.class)
    public ResponseEntity<MessageResponse> handleForbiddenRequest(Exception e) {
        MessageResponse message = MessageResponse.of(AUTHORIZATION_ERROR.getCode(),
            e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }

    @ExceptionHandler({OAuth2EmailNotFoundException.class})
    public ResponseEntity<MessageResponse> handleAccountException(Exception e) {
        MessageResponse message = MessageResponse.of(OAUTH_NO_EXISTENT_EMAIL.getCode() ,
            e.getMessage());

        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler({SpotifyException.class, UnauthorizedException.class})
    public ResponseEntity<MessageResponse> handleSpotifyException(Exception e) {
        MessageResponse message = MessageResponse.of(SPOTIFY_FAILURE.getCode() , e.getMessage());

        return ResponseEntity.internalServerError().body(message);
    }

    @ExceptionHandler({TokenExpirationException.class})
    public ResponseEntity<MessageResponse> handleExpireTokenException(Exception e) {
        MessageResponse message = MessageResponse.of(REFRESH_TOKEN_EXPIRED.getCode() ,
            e.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    @ExceptionHandler({PlaylistException.class})
    public ResponseEntity<MessageResponse> handlePlaylistNotFoundException(Exception e) {
        MessageResponse message = MessageResponse.of(
                PLAYLIST_NOT_FOUND.getCode(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<MessageResponse> handleMethodArgumentTypeMismatchException(Exception e) {
        MessageResponse message = MessageResponse.of(
                REQUEST_TYPE_MISMATCH.getCode(),
                REQUEST_TYPE_MISMATCH.getMessage()
        );

        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<MessageResponse> handleEntityNotFoundException(
            EntityNotFoundException e) {
        MessageResponse message = MessageResponse.of(
                REQUEST_ENTITY_NOT_FOUND.getCode(),
                REQUEST_ENTITY_NOT_FOUND.getMessage()
        );
        return ResponseEntity.badRequest().body(message);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        MessageResponse message = MessageResponse.of(
                REQUEST_TYPE_MISMATCH.getCode(),
                REQUEST_TYPE_MISMATCH.getMessage()
        );

        return ResponseEntity.badRequest().body(message);
    }
}