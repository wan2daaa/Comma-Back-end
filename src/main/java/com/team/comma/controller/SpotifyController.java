package com.team.comma.controller;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	@GetMapping(value = "/artist/{artist}")
	public ArrayList<ArtistResponse> getArtist(@PathVariable String artist) {
		return spotifyService.searchArtist_Sync(artist);
	}
	
	@Operation(summary = "Spotify 곡 찾기", description = "{track} 부분에 찾고자 하는 곡 이름을 넣어 요청을 보내면 해당 검색 결과를 반환")
	@GetMapping(value = "/track/{track}")
	public ArrayList<TrackResponse> getTrack(@PathVariable String track) {
		return spotifyService.searchTrack_Sync(track);
	}
	
	/*
	@GetMapping(value = "/search/{item}/{type}")
	public void searchItem(@PathVariable String item , @PathVariable String type) {
		spotifyService.searchItem_Sync(item , type);
	}
	*/
}
