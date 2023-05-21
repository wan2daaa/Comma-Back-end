package com.team.comma.follow.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.follow.exception.FollowingException;
import com.team.comma.follow.repository.FollowingRepository;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class FollowingServiceTest {
    @InjectMocks
    FollowingService followingService;

    @Mock
    FollowingRepository followingRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("사용자 차단 성공")
    public void blockUserSuccess() {
        // given
        doNothing().when(followingRepository).blockFollowedUser("toUserEmail" , "toUserEmail");
        doReturn("toUserEmail").when(jwtTokenProvider).getUserPk("token");

        // when
        MessageResponse result = followingService.blockFollowedUser("token" , "toUserEmail");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    @DisplayName("사용자 차단해제 성공")
    public void unblockUserSuccess() {
        // given
        doNothing().when(followingRepository).unblockFollowedUser("toUserEmail" , "toUserEmail");
        doReturn("toUserEmail").when(jwtTokenProvider).getUserPk("token");

        // when
        MessageResponse result = followingService.unblockFollowedUser("token" , "toUserEmail");

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    @DisplayName("팔로우 추가 실패 _ 이미 팔로우 한 사용자")
    public void followFail_existFollowUser() {
        // given
        doReturn(Optional.of(User.builder().build())).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "toUserEmail");
        doReturn("toUserEmail").when(jwtTokenProvider).getUserPk("token");

        // when
        Throwable thrown = catchThrowable(() -> followingService.addFollow("token" , "toUserEmail"));

        // when
        assertThat(thrown).isInstanceOf(FollowingException.class).hasMessage("이미 팔로우중인 사용자입니다.");
    }

    @Test
    @DisplayName("팔로우 추가 실패 _ 사용자 조회 실패")
    public void followFail_notFoundToUser() {
        // given
        doReturn(Optional.empty()).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "fromUserEmail");
        doReturn("fromUserEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(userRepository).findByEmail("toUserEmail");

        // when
        Throwable thrown = catchThrowable(() -> followingService.addFollow("token" , "toUserEmail"));

        // when
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("해당 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("팔로우 추가 실패 _ 대상 사용자 조회 실패")
    public void followFail_notFoundFromUser() {
        // given
        doReturn("fromUserEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "fromUserEmail");
        doReturn(Optional.of(User.builder().build())).when(userRepository).findByEmail("toUserEmail");
        doReturn(Optional.empty()).when(userRepository).findByEmail("fromUserEmail");

        // when
        Throwable thrown = catchThrowable(() -> followingService.addFollow("token" , "toUserEmail"));

        // when
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("대상 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("팔로우 추가 실패 _ 차단된 사용자")
    public void followFail_isBlockedUser() {
        // given
        doReturn("fromUserEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "fromUserEmail");
        doReturn(Optional.of(User.builder().build())).when(followingRepository).getBlockedUser("toUserEmail" , "fromUserEmail");

        // when
        Throwable thrown = catchThrowable(() -> followingService.addFollow("token" , "toUserEmail"));

        // when
        assertThat(thrown).isInstanceOf(FollowingException.class).hasMessage("차단된 사용자입니다.");
    }

    @Test
    @DisplayName("팔로우 여부 확인 _ 거짓")
    public void isfollow_false() {
        // given
        doReturn("fromUserEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.empty()).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "fromUserEmail");

        // when
        MessageResponse result = followingService.isFollowedUser("token" , "toUserEmail");

        // then
        assertThat(result.getData()).isEqualTo(false);
    }

    @Test
    @DisplayName("팔로우 여부 확인 _ 참")
    public void isfollow_true() {
        // given
        doReturn("fromUserEmail").when(jwtTokenProvider).getUserPk("token");
        doReturn(Optional.of(User.builder().build())).when(followingRepository).getFollowedMeUserByEmail("toUserEmail" , "fromUserEmail");

        // when
        MessageResponse result = followingService.isFollowedUser("token" , "toUserEmail");

        // then
        assertThat(result.getData()).isEqualTo(true);
    }

}
