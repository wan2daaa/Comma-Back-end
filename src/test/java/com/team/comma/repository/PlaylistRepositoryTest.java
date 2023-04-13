package com.team.comma.repository;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Playlist;
import com.team.comma.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;  //자동 import되지 않음

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlaylistRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    final String userEmail = "email@naver.com";

    @Test
    public void 플레이리스트조회_실패_데이터없음(){
        // given

        // when
        final List<Playlist> result = playlistRepository.findAllByUser_Email(userEmail);

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 플레이리스트조회_성공_2(){
        // given
        userRepository.save(getUser());
        final User user = userRepository.findByEmail(userEmail);

        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트1"));
        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트2"));

        // when
        final List<Playlist> result = playlistRepository.findAllByUser_Email(userEmail);

        // then
        assertThat(result.size()).isEqualTo(2);
    }


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

}
