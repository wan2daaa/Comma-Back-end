package com.team.comma.spotify.search.service;

import com.neovisionaries.i18n.CountryCode;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.history.dto.HistoryRequest;
import com.team.comma.spotify.history.service.HistoryService;
import com.team.comma.spotify.search.dto.ArtistResponse;
import com.team.comma.spotify.search.exception.SpotifyException;
import com.team.comma.spotify.search.support.SpotifyAuthorization;
import com.team.comma.spotify.search.support.SpotifySearchCommand;
import com.team.comma.spotify.track.dto.TrackResponse;
import com.team.comma.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.browse.miscellaneous.GetAvailableGenreSeedsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import javax.security.auth.login.AccountException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static com.team.comma.spotify.track.dto.TrackResponse.createTrackResponse;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SpotifyAuthorization spotifyAuthorization;
    private final SpotifySearchCommand spotifySearchCommand;
    private final HistoryService historyService;
    private final UserService userService;

    public MessageResponse searchArtistList(String artistName , String token) throws AccountException {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(artistName).build();
        Object executeResult = spotifySearchCommand.executeCommand(searchArtistsRequest);

        if(executeResult instanceof SpotifyApi) {
            return searchArtistList(artistName , token);
        }

        Paging<Artist> artistsPaging = (Paging<Artist>) executeResult;

        ArrayList<ArtistResponse> result = new ArrayList<>();
        for (Artist artist : artistsPaging.getItems()) {
            result.add(ArtistResponse.createArtistResponse(artist));
        }

        addHistory(artistName , token);

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public MessageResponse searchTrackList(String trackName , String token) throws AccountException {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        SearchTracksRequest searchTrackRequest = spotifyApi.searchTracks(trackName).build();

        Object executeResult = spotifySearchCommand.executeCommand(searchTrackRequest);
        if(executeResult instanceof SpotifyApi) {
            return searchTrackList(trackName , token);
        }

        Paging<Track> artistsPaging = (Paging<Track>) executeResult;
        ArrayList<TrackResponse> result = new ArrayList<>();
        for (Track track : artistsPaging.getItems()) {
            result.add(createTrackResponse(track));
        }

        addHistory(trackName , token);

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public void addHistory(String history , String token) throws AccountException {
        HistoryRequest request = HistoryRequest.builder().searchHistory(history).build();

        historyService.addHistory(request , token);
    }

    public MessageResponse searchGenreList() {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        GetAvailableGenreSeedsRequest genres = spotifyApi.getAvailableGenreSeeds().build();

        Object executeResult = spotifySearchCommand.executeCommand(genres);
        if(executeResult instanceof SpotifyApi) {
            return searchGenreList();
        }

        String[] result = (String[]) executeResult;
        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public MessageResponse searchArtistListByYear(int offset) {
        int year = LocalDate.now().getYear();
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        SearchArtistsRequest artists = spotifyApi.searchArtists(String.format("year:%d", year))
                .offset(offset)
                .limit(10)
                .market(CountryCode.KR)
                .build();

        Object executeResult = spotifySearchCommand.executeCommand(artists);
        if(executeResult instanceof SpotifyApi) {
            return searchArtistListByYear(offset);
        }

        Paging<Artist> artistPaging = (Paging<Artist>) executeResult;
        ArrayList<String> artistNames = new ArrayList<>();
        for (Artist artist : artistPaging.getItems()) {
            artistNames.add(artist.getName());
        }

        return MessageResponse.of(REQUEST_SUCCESS ,artistNames);
    }

    public MessageResponse searchRecommendation(String accessToken) throws AccountException {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        String favoriteGenre = getFourRandomFavoriteGenreByUser(accessToken);
        String favoriteArtist = getOneRandomFavoriteArtistByUser(accessToken);
        GetRecommendationsRequest recommend = spotifyApi.getRecommendations()
                .seed_genres(favoriteGenre)
                .seed_artists(favoriteArtist)
                .market(CountryCode.KR)
                .limit(10).build();

        Object executeResult = spotifySearchCommand.executeCommand(recommend);
        if(executeResult instanceof SpotifyApi) {
            return searchRecommendation(accessToken);
        }

        Recommendations recommendations = (Recommendations) executeResult;
        ArrayList<TrackResponse> result = new ArrayList<>();
        for(TrackSimplified track : recommendations.getTracks()) {
            result.add(createTrackResponse(searchTrackById(track.getId())));
        }

        return MessageResponse.of(REQUEST_SUCCESS , result);
    }

    public String getFourRandomFavoriteGenreByUser(String token) throws AccountException {
        List<String> genreList = userService.getFavoriteGenreList(token);

        if(genreList.isEmpty()) {
            throw new SpotifyException("사용자 관심 장르를 찾을 수 없습니다.");
        }

        return chooseAndCombineFourRandomGenres(genreList);
    }

    public String chooseAndCombineFourRandomGenres(List<String> genreList) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for(int i = 0; i < 4 ; i++) {
            int randomNumber = random.nextInt(genreList.size());
            sb.append(genreList.get(randomNumber)).append(",");
        }

        sb.delete(sb.length() - 1 , sb.length());
        return sb.toString();
    }

    public String getOneRandomFavoriteArtistByUser(String token) throws AccountException {
        List<String> artistList = userService.getFavoriteArtistList(token);
        Random random = new Random();

        if(artistList.isEmpty()) {
            throw new SpotifyException("사용자 관심 아티스트를 찾을 수 없습니다.");
        }

        return searchArtistByArtistName(artistList.get(random.nextInt(artistList.size()))).getId();
    }

    public Artist searchArtistByArtistName(String artist) {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(artist).limit(1).build();

        Object executeResult = spotifySearchCommand.executeCommand(searchArtistsRequest);

        if(executeResult instanceof SpotifyApi) {
            return searchArtistByArtistName(artist);
        }

        Paging<Artist> artistsPaging = (Paging<Artist>) executeResult;
        Artist[] artistData = artistsPaging.getItems();

        if(artistsPaging.getTotal() == 0) {
            throw new SpotifyException("아티스트를 찾을 수 없습니다.");
        }

        return artistData[0];
    }

    public Track searchTrackById(String id) {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        GetTrackRequest request = spotifyApi.getTrack(id).build();

        Object executeResult = spotifySearchCommand.executeCommand(request);

        if(executeResult instanceof SpotifyApi) {
            return searchTrackById(id);
        }

        Track track = (Track) executeResult;

        return track;
    }

}
