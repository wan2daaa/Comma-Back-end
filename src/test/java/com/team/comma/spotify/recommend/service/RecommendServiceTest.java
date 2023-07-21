package com.team.comma.spotify.recommend.service;

import static com.team.comma.common.constant.ResponseCodeEnum.PLAYLIST_NOT_FOUND;
import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.recommend.constant.RecommendType;
import com.team.comma.spotify.recommend.domain.Recommend;
import com.team.comma.spotify.recommend.dto.RecommendRequest;
import com.team.comma.spotify.recommend.repository.RecommendRepository;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class RecommendServiceTest {

    @InjectMocks
    private RecommendService recommendService;
    @Mock
    private RecommendRepository recommendRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private String token = "accessToken";
    @Test
    void 플레이리스트_추천_저장_실패_추천보낸사용자정보찾을수없음() {
        // given
        final RecommendRequest recommendRequest = buildRequest();

        // when
        final Throwable thrown = catchThrowable(() -> recommendService.addRecommend(token, recommendRequest));

        // then
        assertThat(thrown.getMessage()).isEqualTo("추천 보낸 사용자 정보가 올바르지 않습니다.");

    }

    @Test
    void 플레이리스트_추천_저장_실패_추천받는사용자정보찾을수없음() {
        // given
        final User fromUser = buildUserWithEmail("fromUser");
        final Optional<User> optionalUser = Optional.of(fromUser);
        doReturn(optionalUser).when(userRepository).findByEmail(fromUser.getEmail());
        doReturn(fromUser.getEmail()).when(jwtTokenProvider).getUserPk(token);

        final RecommendRequest recommendRequest = buildRequest();

        // when
        final Throwable thrown = catchThrowable(() -> recommendService.addRecommend(token, recommendRequest));

        // then
        assertThat(thrown.getMessage()).isEqualTo("추천 받는 사용자 정보가 올바르지 않습니다.");

    }

    @Test
    void 플레이리스트_추천_저장_실패_플레이리스트찾을수없음() {
        // given
        final User fromUser = buildUserWithEmail("fromUser");
        final Optional<User> optionalUser = Optional.of(fromUser);
        doReturn(optionalUser).when(userRepository).findByEmail(fromUser.getEmail());
        doReturn(fromUser.getEmail()).when(jwtTokenProvider).getUserPk(token);

        final RecommendRequest recommendRequest = buildRequest();

        final User toUser = buildUserWithEmail(recommendRequest.getRecommendToEmail());
        final Optional<User> optionalToUser = Optional.of(toUser);
        doReturn(optionalToUser).when(userRepository).findByEmail(toUser.getEmail());

        // when
        final Throwable thrown = catchThrowable(() -> recommendService.addRecommend(token, recommendRequest));

        // then
        assertThat(thrown.getMessage()).isEqualTo(PLAYLIST_NOT_FOUND.getMessage());

    }

    @Test
    void 플레이리스트_추천_저장_성공() throws Exception {
        // given
        final User fromUser = buildUserWithEmail("fromUser");
        final Optional<User> optionalUser = Optional.of(fromUser);
        doReturn(optionalUser).when(userRepository).findByEmail(fromUser.getEmail());
        doReturn(fromUser.getEmail()).when(jwtTokenProvider).getUserPk(token);

        final RecommendRequest recommendRequest = buildRequest();

        final User toUser = buildUserWithEmail(recommendRequest.getRecommendToEmail());
        final Optional<User> optionalToUser = Optional.of(toUser);
        doReturn(optionalToUser).when(userRepository).findByEmail(toUser.getEmail());

        final Playlist playlist = buildPlaylistWithId(recommendRequest.getRecommendPlaylistId());
        final Optional<Playlist> optionalPlaylist = Optional.of(playlist);
        doReturn(optionalPlaylist).when(playlistRepository).findById(playlist.getId());

        // when
        final MessageResponse result = recommendService.addRecommend(token, recommendRequest);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    private User buildUserWithEmail(String email) {
        return User.builder()
                .email(email)
                .build();
    }

    private Playlist buildPlaylistWithId(long id) {
        return Playlist.builder()
                .id(id)
                .alarmFlag(true)
                .build();
    }

    private RecommendRequest buildRequest() {
        return RecommendRequest.builder()
                .recommendPlaylistId(1L)
                .recommendType(RecommendType.FOLLOWING)
                .recommendToEmail("toUserEmail")
                .comment("test recommend")
                .build();
    }

    private Recommend buildRecommend() {
        return Recommend.builder()
                .build();
    }
}
