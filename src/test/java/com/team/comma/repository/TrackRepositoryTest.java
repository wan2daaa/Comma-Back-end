package com.team.comma.repository;
import com.team.comma.domain.Track;
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

    @Test
    public void 곡조회_실패_데이터없음(){
        // given

        // when
        final List<Track> result = trackRepository.findAllById(1234L);

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 곡조회_성공(){
        // given
        final Track track = trackRepository.save(getTrack("track test"));

        // when
        final List<Track> result = trackRepository.findAllById(track.getId());

        // then
        assertThat(result.size()).isNotNull();
    }

    private Track getTrack(String title) {
        return Track.builder()
                .trackTitle(title)
                .build();
    }

}
