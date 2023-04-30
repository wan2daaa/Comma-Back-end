package com.team.comma.spotify.search.service;

import com.team.comma.spotify.search.dto.ArtistResponse;
import com.team.comma.spotify.search.service.SpotifyService;
import com.team.comma.spotify.track.dto.TrackResponse;
import com.team.comma.util.jwt.support.CreationAccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class SpotifyServiceTest {

    @InjectMocks
    private SpotifyService spotifyService;

    SpotifyApi spotifyApi = null;

    @BeforeEach
    public void reissueAccessToken() {
        CreationAccessToken accessToken = new CreationAccessToken();

        spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken.accessToken()).build();
    }

    @Test
    @DisplayName("유효하지 않은 토큰")
    public void exceptionUnauthorized() {
        // given
        SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken("Token").build();
        SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists("unknown Artist")
            .build();

        // when
        Throwable thrown = catchThrowable(() -> searchArtistsRequest.execute());

        // then
        assertThat(thrown).isInstanceOf(UnauthorizedException.class)
            .hasMessageContaining("Invalid access token");
    }

    @Test
    @DisplayName("새로운 토큰 발급")
    public void reissueToken() {
        // given
        CreationAccessToken token = new CreationAccessToken();

        // when
        Throwable thrown = catchThrowable(() -> token.accessToken());

        // then
        assertThat(thrown).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("가수 목록 API 호출")
    public void executeArtistList() {
        // given

        // when
        Throwable thrown = catchThrowable(() -> spotifyService.searchArtist_Sync("justin"));

        // then
        assertThat(thrown).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("가수 목록 가져오기")
    public void getArtistList() {
        // given
        final String artists = "Justin Bieber";

        // when
        ArrayList<ArtistResponse> result = spotifyService.searchArtist_Sync(artists);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("트랙 목록 API 호출")
    public void executeTrackList() {
        // given

        // when
        Throwable thrown = catchThrowable(() -> spotifyService.searchTrack_Sync("Hello"));

        // then
        assertThat(thrown).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("트랙 목록 가져오기")
    public void getTrackList() {
        // given
        final String track = "Hello";

        // when
        ArrayList<TrackResponse> result = spotifyService.searchTrack_Sync(track);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("장르 정보 가져오기")
    public void getGenresList() {
        // given

        // when
        String[] result = spotifyService.getGenres();

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("연도 별 아티스트 가져오기")
    public void getArtistByYear() {
        // given

        // when
        String[] result = spotifyService.getArtistByYear(0);
        // then
        assertThat(result).isNotNull();
    }
	
	/*
	@Test
	@DisplayName("트랙 목록 API 호출")
	public void executeItemList() {
		// given

		// when
		Throwable thrown = catchThrowable(() -> spotifyService.searchItem_Sync("Hello" , "track"));

		// then
		assertThat(thrown).doesNotThrowAnyException();
	}
	*/
}
