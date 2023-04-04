package com.team.comma.service;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import com.team.comma.dto.ArtistResponse;
import com.team.comma.dto.TrackResponse;
import com.team.comma.exception.SpotifyException;
import com.team.comma.spotify.CreateAccessToken;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

@Service
public class SpotifyService {

	SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken("Token").build();

	public void refreshAccessToken() {
		CreateAccessToken accessToken = new CreateAccessToken();

		spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken.accesstoken()).build();
	}

	public ArrayList<ArtistResponse> searchArtist_Sync(String artist) {
		SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(artist).build();
//      .market(CountryCode.SE)
//      .limit(10)
//      .offset(0)
//      .includeExternal("audio")
		
		ArrayList<ArtistResponse> result = new ArrayList<>();
		
		try {
			Paging<Artist> artistsPaging = searchArtistsRequest.execute();

			for(Artist at : artistsPaging.getItems()) {
				result.add(ArtistResponse.builder()
						.id(at.getId())
						.uri(at.getUri())
						.name(at.getName())
						.externalUrls(at.getExternalUrls().getExternalUrls().get("spotify"))
						.genres(at.getGenres())
						.image(at.getImages())
						.href(at.getHref())
						.build());
			}
			
			return result;
		} catch (UnauthorizedException e) { // 토큰이 유효하지 않을 때..
			System.out.println("Token is Expire.. Token reissue.");
			refreshAccessToken();
			return searchArtist_Sync(artist);
		} catch (IOException | ParseException | SpotifyWebApiException e) {
			System.out.println("Exceptipn.. : " + e);
			throw new SpotifyException(e.getMessage());
		}
		
	}

	public ArrayList<TrackResponse> searchTrack_Sync(String track) {
		SearchTracksRequest searchTrackRequest = spotifyApi.searchTracks(track).build();
//      .market(CountryCode.SE)
//      .limit(10)
//      .offset(0)
//      .includeExternal("audio")

		ArrayList<TrackResponse> result = new ArrayList<TrackResponse>();
		
		try {
			Paging<Track> artistsPaging = searchTrackRequest.execute();

			for (Track tracks : artistsPaging.getItems()) {
				result.add(TrackResponse.builder()
						.id(tracks.getId())
						.name(tracks.getName())
						.uri(tracks.getUri())
						.artists(tracks.getArtists())
						.previewUrl(tracks.getPreviewUrl())
						.href(tracks.getHref())
						.build());
			}
			
			return result;

		} catch (UnauthorizedException e) { // 토큰이 유효하지 않을 때..
			System.out.println("Token is Expire.. Token reissue.");
			refreshAccessToken();
			return searchTrack_Sync(track);
		} catch (IOException | ParseException | SpotifyWebApiException e) {
			System.out.println("Exception.. : " + e);
			throw new SpotifyException(e.getMessage());
		}
	}

	/*
	public void searchItem_Sync(String item, String type) {
		String types = ModelObjectType.TRACK.getType(); // ARTIST, TRACK, ALBUM , AUDIO_FEATURES , EPISODE , GENRE ,
															// PLAYLIST , SHOW , TRACK , USER
		SearchItemRequest searchItemRequest = spotifyApi.searchItem(item, types).build();
//      .market(CountryCode.SE)
//      .limit(10)
//      .offset(0)
//      .includeExternal("audio")

		try {
			final SearchResult searchResult = searchItemRequest.execute();

			System.out.println("Total tracks: " + searchResult.getTracks().getTotal());
			
			for(Track tracks : searchResult.getTracks().getItems()) {
				System.out.println("나눔..-----------");
				System.out.println(tracks.getId()); // 곡 ID
				System.out.println(tracks.getName()); // 제목
				System.out.println(tracks.getArtists()); // 가수
				System.out.println(tracks.getPreviewUrl()); // 미리 듣기
				System.out.println(tracks.getUri());
				System.out.println(tracks.getHref()); // 재생 ( 토큰 필요 )
			}
			
		}catch (UnauthorizedException e) { // 토큰이 유효하지 않을 때..
			System.out.println("Token is Expire.. Token reissue.");
			refreshAccessToken();
			searchItem_Sync(item , type);
		} catch (IOException | SpotifyWebApiException | ParseException | NullPointerException e) {
			System.out.println("Error: " + e.getMessage());
			throw new SpotifyException(e.getMessage());
		}
	}
	*/
}
