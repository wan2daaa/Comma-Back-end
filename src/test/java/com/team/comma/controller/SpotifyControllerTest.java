package com.team.comma.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.comma.dto.ArtistResponse;
import com.team.comma.dto.TrackResponse;
import com.team.comma.service.SpotifyService;

import javax.xml.transform.Result;

/*
 * https://developer.spotify.com/documentation/web-api/reference/get-an-artist
 */

@ExtendWith(MockitoExtension.class)
public class SpotifyControllerTest {

	@InjectMocks
	SpotifyController spotifyController;

	@Mock
	SpotifyService spotifyService;

	MockMvc mockMvc;
	Gson gson;

	@BeforeEach
	public void init() {
		mockMvc = MockMvcBuilders.standaloneSetup(spotifyController).build();
		gson = new Gson();
	}

	@Test
	@DisplayName("가수명 검색하기")
	public void searchBySinger() throws Exception {
		// given
		final String api = "/spotify/artist/박효신";
		
		doReturn(new ArrayList<ArtistResponse>(Arrays.asList(
				ArtistResponse.builder().build(),
				ArtistResponse.builder().build(),
				ArtistResponse.builder().build())))
		.when(spotifyService).searchArtist_Sync(any(String.class));

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));

		// then
		resultActions.andExpect(status().isOk());
		final ArrayList<ArtistResponse> result = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
				new TypeToken<ArrayList<ArtistResponse>>() {}.getType());

		assertThat(result.size()).isEqualTo(3);
	}
	
	@Test
	@DisplayName("트랙명 검색하기")
	public void searchByTrack() throws Exception {
		// given
		final String api = "/spotify/track/hello";
		
		doReturn(new ArrayList<TrackResponse>(Arrays.asList(
				TrackResponse.builder().build(),
				TrackResponse.builder().build(),
				TrackResponse.builder().build()
				)))
		.when(spotifyService).searchTrack_Sync(any(String.class));
		
		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));
		
		// then
		resultActions.andExpect(status().isOk());
		final ArrayList<TrackResponse> result = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), 
				new TypeToken<ArrayList<ArtistResponse>>() {}.getType());
		
		assertThat(result.size()).isEqualTo(3);
	}

	@Test
	@DisplayName("장르 목록 가져오기")
	public void getGenresList() throws Exception {
		// given
		final String api = "/spotify/genre";
		doReturn(new String[] {"A" , "B" , "C"}).when(spotifyService).getGenres();

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));

		// then
		resultActions.andExpect(status().isOk());
		final String[] result = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8) ,
				String [].class);

		assertThat(result.length).isEqualTo(3);
	}

	@Test
	@DisplayName("연도별 아티스트 목록 가져오기")
	public void getArtistListByYear() throws Exception {
		// given
		final String api = "/spotify/artist?year=2023&offset=0";
		doReturn(new ArrayList<String>(Arrays.asList("A" , "B" , "C" , "D" , "E"))).when(spotifyService)
				.getArtistByYear(2023 , 0);
		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));
		// then
		resultActions.andExpect(status().isOk());
		final ArrayList<String> result = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8) ,
				new TypeToken<ArrayList<String>>() {}.getType());

		assertThat(result.size()).isEqualTo(5);
	}

}
