package com.team.comma.spotify.recommend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.recommend.constant.RecommendType;
import com.team.comma.spotify.recommend.domain.Recommend;
import com.team.comma.user.domain.User;
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestConfig.class)
public class RecommendRepositoryTest {

    @Autowired
    private RecommendRepository recommendRepository;

    @Test
    void 플레이리스트_추천_저장() {
        // given
        final Recommend recommend = buildRecommend(RecommendType.FOLLOWING);

        // when
        final Recommend result = recommendRepository.save(recommend);

        // then
        assertThat(result.getComment()).isEqualTo("test recommend");
    }

    Recommend buildRecommend(RecommendType type) {
        return Recommend.builder()
                .fromUser(User.builder().build())
                .toUser(User.builder().build())
                .recommendType(type)
                .comment("test recommend")
                .playlist(Playlist.builder().build())
                .build();
    }
}
