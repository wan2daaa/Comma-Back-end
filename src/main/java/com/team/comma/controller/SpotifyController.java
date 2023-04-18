package com.team.comma.controller;

import java.util.ArrayList;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.team.comma.dto.ArtistResponse;
import com.team.comma.dto.TrackResponse;
import com.team.comma.service.SpotifyService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

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

    @Operation(summary = "Spotify에 존재하는 모든 장르 가져오기", description = "uri 요청 시 Spotify에 존재하는 모든 장르 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "조회 성공했을 때 배열에다가 담아 반환" , content = @Content(schema = @Schema(implementation = String [].class)))
    })
    @GetMapping("/genre")
    public ResponseEntity<String[]> getGenres() {
        return ResponseEntity.ok().body(spotifyService.getGenres());
    }

    @Operation(summary = "Spotify 연도 별 아티스트 가져오기", description = "쿼리 파라미터 year 로 연도를 설정하고 offset 으로 페이지를 조절 \n 예시 : /spotify/artist?year=2023&offset=0 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "조회 성공했을 때 배열에다가 담아 반환" , content = @Content(schema = @Schema(implementation = ArrayList.class)))
    })
    @GetMapping("/artist")
    public ResponseEntity<ArrayList<String>> getArtistByYear(@RequestParam Long year, @RequestParam int offset) {
        return ResponseEntity.ok().body(spotifyService.getArtistByYear(year, offset));
    }

	/*
	@GetMapping(value = "/search/{item}/{type}")
	public void searchItem(@PathVariable String item , @PathVariable String type) {
		spotifyService.searchItem_Sync(item , type);
	}
	*/
}
