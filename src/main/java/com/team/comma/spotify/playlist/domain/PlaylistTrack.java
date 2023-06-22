package com.team.comma.spotify.playlist.domain;

import com.team.comma.spotify.track.domain.Track;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "playlist_track_tb")
public class PlaylistTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer playSequence;

    @ColumnDefault("false")
    private Boolean playFlag;
    @ColumnDefault("false")
    private Boolean trackAlarmFlag;

    @JoinColumn(name = "playlist_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Playlist playlist;

    @JoinColumn(name = "track_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Track track;


}
