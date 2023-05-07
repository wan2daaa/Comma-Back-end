package com.team.comma.spotify.search.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RequestMapping(value = "/spotify")
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService spotifyService;

    @GetMapping("/artist/{artist}")
    public ResponseEntity<MessageResponse> searchArtistList(@PathVariable String artist , @CookieValue("accessToken") String accessToken) throws AccountException {
        return ResponseEntity.ok().body(spotifyService.searchArtistList(artist , accessToken));
    }

    @GetMapping("/track/{track}")
    public ResponseEntity<MessageResponse> searchTrackList(@PathVariable String track , @CookieValue("accessToken") String accessToken) throws AccountException {
        return ResponseEntity.ok().body(spotifyService.searchTrackList(track , accessToken));
    }

    @GetMapping("/genre")
    public ResponseEntity<MessageResponse> searchGenreList() {
        return ResponseEntity.ok().body(spotifyService.searchGenreList());
    }

    @GetMapping("/artist")
    public ResponseEntity<MessageResponse> searchArtistListByYear(@RequestParam int offset) {
        return ResponseEntity.ok().body(spotifyService.searchArtistListByYear(offset));
    }
}
