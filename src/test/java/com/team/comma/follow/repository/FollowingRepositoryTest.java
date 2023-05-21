package com.team.comma.follow.repository;

import com.team.comma.follow.domain.Following;
import com.team.comma.user.domain.User;
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FollowingRepositoryTest {
    @Autowired
    private FollowingRepository followingRepository;

    @Test
    @DisplayName("나를 팔로우한 사용자 탐색")
    public void searchFollowedMeUser() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .userFrom(fromUser)
                .blockFlag(false)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        User result = followingRepository.getFollowedMeUserByEmail("toEmail" , "fromEmail").orElse(null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("toEmail");
    }

    @Test
    @DisplayName("나를 팔로우한 사용자 탐색 없음")
    public void searchFollowedMeUser_none() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .userFrom(fromUser)
                .blockFlag(false)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);


        // when
        User result = followingRepository.getFollowedMeUserByEmail("toEmail" , "fromEmails").orElse(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("나를 팔로우한 사람 차단")
    public void blockFollowedUser() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(false)
                .userFrom(fromUser)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        followingRepository.blockFollowedUser("toEmail" , "fromEmail");

        // then
        User result = followingRepository.getBlockedUser("toEmail" , "fromEmail").orElse(null);
        assertThat(result.getEmail()).isEqualTo("toEmail");
    }

    @Test
    @DisplayName("나를 팔로우한 사람 차단 해제")
    public void unblockFollowedUser() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(true)
                .userFrom(fromUser)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        followingRepository.unblockFollowedUser("toEmail" , "fromEmail");

        // then
        User result = followingRepository.getFollowedMeUserByEmail("toEmail" , "fromEmail").orElse(null);
        assertThat(result.getEmail()).isEqualTo("toEmail");
    }

    @Test
    @DisplayName("삭제된 사용자 확인")
    public void isBlockedUser() {
        // given
        User toUser = User.builder()
                .email("toEmail")
                .build();
        User fromUser = User.builder()
                .email("fromEmail")
                .build();

        Following follow = Following.builder()
                .userTo(toUser)
                .blockFlag(true)
                .userFrom(fromUser)
                .build();

        follow.setUserTo(toUser);
        follow.setUserFrom(fromUser);
        followingRepository.save(follow);

        // when
        User result = followingRepository.getBlockedUser("toEmail" , "fromEmail").orElse(null);

        // then
        assertThat(result).isNotNull();
    }

}
