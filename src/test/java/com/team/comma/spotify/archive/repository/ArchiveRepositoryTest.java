package com.team.comma.spotify.archive.repository;

import com.team.comma.spotify.archive.domain.Archive;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.user.domain.User;
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArchiveRepositoryTest {

    @Autowired
    ArchiveRepository archiveRepository;

    @Test
    @DisplayName("아카이브 정보 저장")
    public void saveArchive() {
        // given
        User user = User.builder().build();
        Playlist playlist = Playlist.builder().build();
        Archive archive = Archive.createArchive(user , "content" , playlist);

        // when
        Archive result = archiveRepository.save(archive);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("content");
    }
}
