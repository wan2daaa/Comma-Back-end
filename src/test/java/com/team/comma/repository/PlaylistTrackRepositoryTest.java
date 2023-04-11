package com.team.comma.repository;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
import com.team.comma.domain.Track;
import com.team.comma.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;  //자동 import되지 않음

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlaylistTrackRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    final String userEmail = "email@naver.com";

    @Test
    public void 플레이리스트조회_0(){
        // given

        // when
        List<Playlist> result = playlistRepository.findAllByUser_Email(userEmail);

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 플레이리스트조회_2(){
        // given
        userRepository.save(getUser());
        User user = userRepository.findByEmail(userEmail);
        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트1"));
        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트2"));

        // when
        List<Playlist> result = playlistRepository.findAllByUser_Email(userEmail);

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void 플레이리스트_곡조회_0(){
        // given
        userRepository.save(getUser());
        User user = userRepository.findByEmail(userEmail);

        Playlist playlist = playlistRepository.save(getPlaylist(user, "테스트 플레이리스트"));

        // when
        List<PlaylistTrack> result = playlistTrackRepository.findAllByPlaylist_Id(playlist.getId());

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 플레이리스트_곡조회_2(){
        // given
        userRepository.save(getUser());
        User user = userRepository.findByEmail(userEmail);

        Playlist playlist = playlistRepository.save(getPlaylist(user, "테스트 플레이리스트"));

        Track track1 = trackRepository.save(getTrack("track1"));
        Track track2 = trackRepository.save(getTrack("track2"));

        playlistTrackRepository.save(getPToT(playlist,track1));
        playlistTrackRepository.save(getPToT(playlist,track2));

        // when
        List<PlaylistTrack> result = playlistTrackRepository.findAllByPlaylist_Id(playlist.getId());

        // then
        assertThat(result.size()).isEqualTo(2);
    }

//    @Test
//    public void 플레이리스트조회_곡상세조회(){
//        // given
//        userRepository.save(getUser());
//        User user = userRepository.findByEmail(userEmail);
//        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트1"));
//        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트2"));
//
//        // when
//        List<Playlist> result = playlistRepository.findAllByUser_Email(userEmail);
//
//        // then
//        assertThat(result.size()).isEqualTo(2);
//    }

    private User getUser() {
        return User.builder()
                .email(userEmail)
                .type(UserType.GeneralUser)
                .role(UserRole.USER)
                .build();
    }

    private Playlist getPlaylist(User user, String title) {
        return Playlist.builder()
                .playlistTitle(title)
                .user(user)
                .build();
    }

    private Track getTrack(String title) {
        return Track.builder()
                .trackTitle(title)
                .build();
    }

    private PlaylistTrack getPToT(Playlist playlist,Track track) {
        return PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .build();
    }
}
