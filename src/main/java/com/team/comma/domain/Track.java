package com.team.comma.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "track_tb")
public class Track extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackTitle;

    private Integer durationMs;

    private String artistNames;

    private String albumName;

    private String albumImageUrl;

    private Boolean albumFlag;

    @Setter
    @JoinColumn(name = "playlist_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Playlist playlist;

}
