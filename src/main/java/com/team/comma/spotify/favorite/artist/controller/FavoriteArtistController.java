package com.team.comma.spotify.favorite.artist.controller;

import com.team.comma.spotify.favorite.artist.dto.FavoriteArtistRequest;
import com.team.comma.spotify.favorite.artist.service.FavoriteArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites/artists")
public class FavoriteArtistController {

    private final FavoriteArtistService favoriteArtistService;

    @GetMapping
    public ResponseEntity isFavoriteArtist(@CookieValue String accessToken
            , @RequestBody FavoriteArtistRequest favoriteArtistRequest) throws AccountException {
        return ResponseEntity.ok()
                .body(favoriteArtistService.isFavoriteArtist(accessToken , favoriteArtistRequest.getArtistName()));
    }

    @PostMapping
    public ResponseEntity addFavoriteArtist(@CookieValue String accessToken
            , @RequestBody FavoriteArtistRequest favoriteArtistRequest) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriteArtistService.addFavoriteArtist(accessToken, favoriteArtistRequest.getArtistName()));
    }

    @DeleteMapping
    public ResponseEntity deleteFavoriteArtist(@CookieValue String accessToken
            , @RequestBody FavoriteArtistRequest favoriteArtistRequest) throws AccountException {
        return ResponseEntity.ok()
                .body(favoriteArtistService.deleteFavoriteArtist(accessToken , favoriteArtistRequest.getArtistName()));
    }
}
