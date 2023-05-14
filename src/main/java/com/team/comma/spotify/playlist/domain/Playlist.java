package com.team.comma.spotify.playlist.domain;

import com.team.comma.spotify.playlist.dto.PlaylistRequest;
import com.team.comma.spotify.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.user.domain.User;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "playlist_tb")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String playlistTitle;

    private LocalTime alarmStartTime;

    @ColumnDefault("false")
    private Boolean alarmFlag;

    private Integer listSequence;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "playlist")
    @Builder.Default
    private List<PlaylistTrack> playlistTrackList = new ArrayList<>();

    public void addPlaylistTrack(Track track) {

        PlaylistTrack playlistTrack = PlaylistTrack.builder()
            .playlist(this)
            .track(track)
            .build();

        playlistTrackList.add(playlistTrack);
    }

    public void updatePlaylist(PlaylistUpdateRequest playlistUpdateRequest) {
        this.playlistTitle = playlistUpdateRequest.getPlaylistTitle();
        this.alarmStartTime = playlistUpdateRequest.getAlarmStartTime();
        this.listSequence = playlistUpdateRequest.getListSequence();
    }
}
