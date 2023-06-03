package com.team.comma.spotify.track.repository;

import com.team.comma.spotify.track.domain.Track;
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TrackRepositoryTest {

    @Autowired
    private TrackRepository trackRepository;

    private String spotifyTrackId = "input ISRC of track";
    @Test
    void 곡_저장() {
        // given

        // when
        final Track result = trackRepository.save(buildTrack("test track"));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTrackTitle()).isEqualTo("test track");
    }

    @Test
    void 곡_조회_실패_곡정보없음() {
        // given

        // when
        final Optional<Track> result = trackRepository.findById(123L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 곡_조회_성공() {
        // given
        final Track track = trackRepository.save(buildTrack("test track"));

        // when
        final Optional<Track> result = trackRepository.findById(track.getId());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(Optional.of(track));
    }

    @Test
    void spotify_track_id로_곡_조회_성공(){
        // given
        final Track track = trackRepository.save(buildTrack("test track"));

        // when
        final Optional<Track> result = trackRepository.findBySpotifyTrackId(spotifyTrackId);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(Optional.of(track));
    }

    @Test
    void spotify_track_id로_곡_조회_실패_곡정보없음(){
        // given

        // when
        final Optional<Track> result = trackRepository.findBySpotifyTrackId(spotifyTrackId);

        // then
        assertThat(result).isEmpty();
    }

    private Track buildTrack(String title) {
        return Track.builder()
            .trackTitle(title)
            .spotifyTrackId(spotifyTrackId)
            .build();
    }

}
