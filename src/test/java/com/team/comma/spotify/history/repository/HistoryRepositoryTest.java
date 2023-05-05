package com.team.comma.spotify.history.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.spotify.history.dto.HistoryResponse;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.team.comma.spotify.history.domain.QHistory.history;
import static com.team.comma.user.domain.QUser.user;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class HistoryRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    private JPAQueryFactory queryFactory;

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
        List<HistoryResponse> result = getHistoryListByUserEmail("email");

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("사용자 History 삭제")
    public void deleteUserHistory() {
        // given
        User userData = User.builder().email("email").password("password").role(UserRole.USER).build();
        userRepository.save(userData);
        userData.addHistory("History01");
        userData.addHistory("History02");
        userData.addHistory("History03");

        // when
        List<HistoryResponse> resultBefore = getHistoryListByUserEmail("email");
        for(HistoryResponse response : resultBefore) {
            queryFactory.update(history)
                    .set(history.delFlag, true)
                    .where(history.id.eq(response.getId()))
                    .execute();
        }
        entityManager.flush();
        entityManager.clear();

        List<HistoryResponse> resultAfter = getHistoryListByUserEmail("email");

        // when
        assertThat(resultBefore.size()).isEqualTo(3);
        assertThat(resultAfter.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("모든 사용자 History 삭제")
    public void deleteUserAllHistory() {
        // given
        User userData = User.builder().email("email").password("password").role(UserRole.USER).build();
        userRepository.save(userData);
        userData.addHistory("History01");
        userData.addHistory("History02");
        userData.addHistory("History03");

        // when
        List<HistoryResponse> resultBefore = getHistoryListByUserEmail("email");

        queryFactory.update(history)
                .set(history.delFlag, true)
                .where(history.user.in(userData))
                .execute();

        entityManager.flush();
        entityManager.clear();

        List<HistoryResponse> resultAfter = getHistoryListByUserEmail("email");

        // then
        assertThat(resultBefore.size()).isEqualTo(3);
        assertThat(resultAfter.size()).isEqualTo(0);
    }

    public List<HistoryResponse> getHistoryListByUserEmail(String userEmail) {
        return queryFactory.select(Projections.constructor(HistoryResponse.class, history.id, history.searchHistory))
                .from(history)
                .innerJoin(history.user, user)
                .where(history.user.email.eq(userEmail).and(history.delFlag.eq(false)))
                .fetch();
    }
}
