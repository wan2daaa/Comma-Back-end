package com.team.comma.spotify.history.repository;

import com.team.comma.spotify.history.dto.HistoryResponse;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HistoryRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    HistoryRepository historyRepository;

    @Test
    @DisplayName("사용자 History 조회")
    public void saveUserHistory() {
        // given
        User user = User.builder().email("email").password("password").role(UserRole.USER).build();
        user.addHistory("History01");
        user.addHistory("History02");
        user.addHistory("History03");
        userRepository.save(user);

        // when
        List<HistoryResponse> result = historyRepository.getHistoryListByUserEmail("email");

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("사용자 History 한개 삭제")
    public void deleteUserHistory() {
        // given
        User userData = User.builder().email("email").password("password").role(UserRole.USER).build();
        userData.addHistory("History01");
        userData.addHistory("History02");
        userData.addHistory("History03");
        userRepository.save(userData);

        // when
        List<HistoryResponse> resultBefore = historyRepository.getHistoryListByUserEmail("email");
        for(HistoryResponse response : resultBefore) {
            historyRepository.deleteHistoryById(response.getId());
            break;
        }

        // when
        List<HistoryResponse> resultAfter = historyRepository.getHistoryListByUserEmail("email");
        assertThat(resultBefore.size()).isEqualTo(3);
        assertThat(resultAfter.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("모든 사용자 History 삭제")
    public void deleteUserAllHistory() {
        // given
        User userData = User.builder().email("email").password("password").role(UserRole.USER).build();
        userData.addHistory("History01");
        userData.addHistory("History02");
        userData.addHistory("History03");
        userRepository.save(userData);

        // when
        List<HistoryResponse> resultBefore = historyRepository.getHistoryListByUserEmail("email");
        historyRepository.deleteAllHistoryByUser(userData);

        // then
        List<HistoryResponse> resultAfter = historyRepository.getHistoryListByUserEmail("email");
        assertThat(resultBefore.size()).isEqualTo(3);
        assertThat(resultAfter.size()).isEqualTo(0);
    }
}
