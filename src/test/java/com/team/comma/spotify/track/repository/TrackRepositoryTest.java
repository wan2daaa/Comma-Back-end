package com.team.comma.spotify.track.repository;

import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import com.team.comma.spotify.track.domain.Track;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;  //자동 import되지 않음

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TrackRepositoryTest {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    @Test
    public void 곡조회_실패_데이터없음() {
        // given

        // when
        final List<Track> result = trackRepository.findAllById(1234L);

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 곡조회_성공() {
        // given
        final Track track = trackRepository.save(buildTrackWithTitle("track test"));

        // when
        final List<Track> result = trackRepository.findAllById(track.getId());

        // then
        assertThat(result.size()).isNotNull();
    }


    private Track buildTrackWithTitle(String title) {
        return Track.builder()
            .trackTitle(title)
            .build();
    }

}
