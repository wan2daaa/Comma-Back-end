package com.team.comma.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "track_tb")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String trackTitle;

    private Integer durationTimeMs;

    @Column(length = 50)
    private String albumImageUrl;

    @Column(length = 50)
    private String spotifyTrackId;

    @Column(length = 50)
    private String spotifyTrackHref;

    @OneToMany(mappedBy = "track")
    private List<TrackArtist> trackArtistList;

    public void setTrackArtistList(TrackArtist trackArtist) {
        getTrackArtistList().add(trackArtist);
        trackArtist.setTrack(this);
    }

}
