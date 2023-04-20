package com.team.comma.domain;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

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

//    @ElementCollection
//    @CollectionTable(name = "track_artist_tb")
//    @Column(name = "artist_name")
//    private List<String> artistNameList;
}
