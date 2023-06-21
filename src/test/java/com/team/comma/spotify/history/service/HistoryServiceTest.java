package com.team.comma.spotify.history.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.history.dto.HistoryRequest;
import com.team.comma.spotify.history.repository.HistoryRepository;
import com.team.comma.user.constant.UserRole;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class HistoryServiceTest {

    @InjectMocks
    HistoryService spotifyHistoryService;

    @Mock
    HistoryRepository spotifyHistoryRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("History 저장 실패 _ Token 으로 사용자 정보를 찾을 수 없음")
    public void addHistoryFail_notFountUserByToken() {
        // given
        String token = "token";
        doReturn("user").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(Optional.empty()).when(userRepository).findByEmail(any(String.class));
        HistoryRequest request = HistoryRequest.builder().searchHistory("history").build();

        // when
        Throwable thrown = catchThrowable(() -> spotifyHistoryService.addHistory(request , token));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("History 저장 성공")
    public void addHistorySuccess() throws AccountException {
        // given
        String token = "token";
        doReturn("user").when(jwtTokenProvider).getUserPk(any(String.class));
        Optional<User> user = createUserEntity();
        doReturn(user).when(userRepository).findByEmail(any(String.class));
        HistoryRequest request = HistoryRequest.builder().searchHistory("history").build();

        // when
        MessageResponse result = spotifyHistoryService.addHistory(request , token);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo("요청이 성공적으로 수행되었습니다.");
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("사용자 Token에 대해 History 가져오기 실패 _ 사용자를 찾을 수 없음")
    public void getHistoryByUserTokenFail_notFountUser() {
        // given
        String token = "token";
        doReturn("user").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(Optional.empty()).when(userRepository).findByEmail(any(String.class));

        // when
        Throwable thrown = catchThrowable(() -> spotifyHistoryService.getHistoryList(token));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자 Token에 대해 History 가져오기")
    public void getHistoryByUserToken() throws AccountException {
        // given
        String token = "token";
        doReturn("user").when(jwtTokenProvider).getUserPk(any(String.class));
        Optional<User> user = createUserEntity();
        doReturn(user).when(userRepository).findByEmail(any(String.class));
        doReturn(Arrays.asList("history1" , "history2" , "history3")).when(spotifyHistoryRepository)
                .getHistoryListByUserEmail(any(String.class));
        // when
        MessageResponse result = spotifyHistoryService.getHistoryList(token);

        // then
        List<String> historyList = (List<String>) result.getData();
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo("요청이 성공적으로 수행되었습니다.");
        assertThat(historyList.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("모든 History 삭제하기 실패 _ 찾을 수 없는 사용자")
    public void deleteAllHistoryFail_notFountUser() {
        // given
        String token = "token";
        doReturn("user").when(jwtTokenProvider).getUserPk(any(String.class));
        doReturn(Optional.empty()).when(userRepository).findByEmail(any(String.class));
        // when
        Throwable thrown = catchThrowable(() -> spotifyHistoryService.deleteAllHistory(token));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("모든 History 삭제하기 성공")
    public void deleteAllHistory() {
        // given
        String token = "token";
        doReturn("user").when(jwtTokenProvider).getUserPk(any(String.class));
        Optional<User> user = createUserEntity();
        doReturn(user).when(userRepository).findByEmail(any(String.class));
        doNothing().when(spotifyHistoryRepository).deleteAllHistoryByUser(any(User.class));

        // when
        Throwable thrown = catchThrowable(() -> spotifyHistoryService.deleteAllHistory(token));

        // when
        assertThat(thrown).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("특정 History 삭제 하기")
    public void deleteHistory() {
        // given
        doNothing().when(spotifyHistoryRepository).deleteHistoryById(20);
        // when

        MessageResponse result = spotifyHistoryService.deleteHistory(20);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo("요청이 성공적으로 수행되었습니다.");

    }

    public Optional<User> createUserEntity() {
        User user = User.builder().email("email").password("password").role(UserRole.USER).build();

        return Optional.of(user);
    }

}
