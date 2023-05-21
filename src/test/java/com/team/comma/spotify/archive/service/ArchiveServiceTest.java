package com.team.comma.spotify.archive.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.archive.dto.ArchiveRequest;
import com.team.comma.spotify.archive.repository.ArchiveRepository;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.Optional;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ArchiveServiceTest {

    @InjectMocks
    ArchiveService archiveService;

    @Mock
    ArchiveRepository archiveRepository;

    @Mock
    PlaylistRepository playlistRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("아카이브 추가 실패 _ 사용자 찾기 실패")
    public void addArchiveFail_notFoundUser() {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(userRepository).findByEmail("userEmail");

        // when
        Throwable thrown = catchThrowable(() -> archiveService.addArchive("token" , null));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("아카이브 추가 실패 _ 플레이리스트 탐색 실패")
    public void addArchiveFail_notFoundPlaylist() {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByEmail("userEmail");
        doReturn(Optional.empty()).when(playlistRepository).findById(0L);
        ArchiveRequest archiveRequest = ArchiveRequest.builder().playlistId(0L).build();

        // when
        Throwable thrown = catchThrowable(() -> archiveService.addArchive("token" , archiveRequest));

        // then
        assertThat(thrown).isInstanceOf(PlaylistException.class).hasMessage("Playlist를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("아카이브 추가 성공")
    public void addArchiveSuccess() throws AccountException {
        // given
        doReturn("userEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByEmail("userEmail");
        doReturn(Optional.of(Playlist.builder().build())).when(playlistRepository).findById(0L);
        ArchiveRequest archiveRequest = ArchiveRequest.builder().playlistId(0L).build();

        // when
        MessageResponse messageResponse = archiveService.addArchive("token" , archiveRequest);

        // then
        assertThat(messageResponse.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
    }
}
