package com.team.comma.spotify.playlist.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.dto.TrackRequest;
import com.team.comma.user.domain.User;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: wan2daaa
 */
@Getter
@Builder
@AllArgsConstructor
public class PlaylistTrackSaveRequestDto {

    private List<Long> playlistIdList;

    private String playlistTitle;

    private LocalTime alarmStartTime;

    private List<TrackRequest> trackList;

    @Setter
    @JsonIgnore
    private int listSequence;

    @Setter
    @JsonIgnore
    private User user;

    @JsonCreator
    public PlaylistTrackSaveRequestDto
        (
            @JsonProperty("playlistTitle") String playlistTitle,
            @JsonProperty("alarmStartTime") LocalTime alarmStartTime,
            @JsonProperty("trackList") List<TrackRequest> trackList
        ) {
        this.playlistTitle = playlistTitle;
        this.alarmStartTime = alarmStartTime;
        this.trackList = trackList;
    }

    public Playlist toPlaylistEntity() {
        return Playlist.builder()
            .playlistTitle(playlistTitle)
            .alarmStartTime(alarmStartTime)
            .user(user)
            .listSequence(listSequence)
            .build();
    }
}
