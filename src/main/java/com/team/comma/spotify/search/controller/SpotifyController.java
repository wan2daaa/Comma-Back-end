package com.team.comma.spotify.search.controller;

import com.team.comma.spotify.search.dto.ArtistResponse;
import com.team.comma.spotify.search.service.SpotifyService;
import com.team.comma.spotify.track.dto.TrackResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequestMapping(value = "/spotify")
@RestController
@RequiredArgsConstructor
public class SpotifyController {

    final private SpotifyService spotifyService;

    @Operation(summary = "Spotify 가수 찾기", description = "{artist} 부분에 찾고자 하는 가수 이름을 넣어 요청을 보내면 해당 검색 결과를 반환")
    @GetMapping("/artist/{artist}")
    public ResponseEntity<ArrayList<ArtistResponse>> getArtist(@PathVariable String artist) {
        return ResponseEntity.ok().body(spotifyService.searchArtist_Sync(artist));
    }

    @Operation(summary = "Spotify 곡 찾기", description = "{track} 부분에 찾고자 하는 곡 이름을 넣어 요청을 보내면 해당 검색 결과를 반환")
    @GetMapping("/track/{track}")
    public ResponseEntity<ArrayList<TrackResponse>> getTrack(@PathVariable String track) {
        return ResponseEntity.ok().body(spotifyService.searchTrack_Sync(track));
    }

    @GetMapping("/genre")
    public ResponseEntity<String[]> getGenres() {
        return ResponseEntity.ok().body(spotifyService.getGenres());
    }

    @GetMapping("/artist")
    public ResponseEntity<String[]> getArtistByYear(@RequestParam int offset) {
        return ResponseEntity.ok().body(spotifyService.getArtistByYear(offset));
    }

	/*
	@GetMapping(value = "/search/{item}/{type}")
	public void searchItem(@PathVariable String item , @PathVariable String type) {
		spotifyService.searchItem_Sync(item , type);
	}
	*/
}
