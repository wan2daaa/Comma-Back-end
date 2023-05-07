package com.team.comma.spotify.search.service;

import com.neovisionaries.i18n.CountryCode;
import com.team.comma.spotify.search.dto.ArtistResponse;
import com.team.comma.spotify.track.dto.TrackResponse;
import com.team.comma.spotify.search.exception.SpotifyException;
import com.team.comma.util.jwt.support.CreationAccessToken;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.browse.miscellaneous.GetAvailableGenreSeedsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class SpotifyService {

    SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken("Token").build();

    public void refreshSpotifyToken() {
        CreationAccessToken creationAccessToken = new CreationAccessToken();

        spotifyApi = new SpotifyApi.Builder().setAccessToken(creationAccessToken.accessToken())
            .build();
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

            for (Artist at : artistsPaging.getItems()) {
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
            refreshSpotifyToken();
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
            refreshSpotifyToken();
            return searchTrack_Sync(track);
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Exception.. : " + e);
            throw new SpotifyException(e.getMessage());
        }
    }

    public String[] getGenres() {
        GetAvailableGenreSeedsRequest genres = spotifyApi.getAvailableGenreSeeds().build();

        try {
            return genres.execute();
        } catch (UnauthorizedException e) {
            refreshSpotifyToken();
            return getGenres();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Exception.. : " + e);
            throw new SpotifyException(e.getMessage());
        }
    }

    public String[] getArtistByYear(int offset) {
        int year = LocalDate.now().getYear();

        SearchArtistsRequest artists = spotifyApi.searchArtists(String.format("year:%d", year))
            .offset(offset)
            .limit(10)
            .market(CountryCode.KR)
            .build();

        ArrayList<String> artistNames = new ArrayList<>();
        try {
            Paging<Artist> artistPaging = artists.execute();
            for (Artist artist : artistPaging.getItems()) {
                artistNames.add(artist.getName());
            }

            return artistNames.toArray(new String[artistNames.size()]);
        } catch (UnauthorizedException e) {
            refreshSpotifyToken();
            return getArtistByYear(offset);
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
