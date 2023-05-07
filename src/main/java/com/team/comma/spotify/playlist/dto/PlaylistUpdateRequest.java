package com.team.comma.spotify.playlist.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.user.domain.User;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: wan2daaa
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistUpdateRequest {

    
    private Long id;

    private String playlistTitle;

    private LocalTime alarmStartTime;

    @JsonIgnore
    @Setter
    private int listSequence;

    @Setter
    @JsonIgnore
    private User user;

    // 프로퍼티 기반 생성자 추가
    @JsonCreator
    public PlaylistUpdateRequest(@JsonProperty("playlistTitle") String playlistTitle,
        @JsonProperty("alarmStartTime") LocalTime alarmStartTime) {
        this.playlistTitle = playlistTitle;
        this.alarmStartTime = alarmStartTime;
    }


    public Playlist toEntity() {
        return Playlist.builder()
            .playlistTitle(playlistTitle)
            .alarmStartTime(alarmStartTime)
            .listSequence(listSequence)
            .user(user)
            .build();
    }
}
