package com.team.comma.spotify.recommend.domain;

import com.team.comma.spotify.recommend.constant.RecommendType;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recommend_tb")
public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RecommendType recommendType;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @JoinColumn(name = "playlist_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Playlist playlist;

    @JoinColumn(name = "fromUser_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User fromUser;

    @JoinColumn(name = "toUser_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User toUser;

}
