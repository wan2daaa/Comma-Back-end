package com.team.comma.repository;

import com.team.comma.dto.PlaylistResponse;
import com.team.comma.entity.UserEntity;
import com.team.comma.entity.UserPlaylist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;  //자동 import되지 않음
import static org.assertj.core.api.InstanceOfAssertFactories.LOCAL_DATE;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MainRepositoryTest {

    @Autowired
    private MainRepository mainRepository;

    @Autowired
    private UserRepository userRepository;

    final String userEmail = "email@naver.com";

    private UserEntity getUserEntity() {
        return UserEntity.builder().email(userEmail).userType(UserEntity.UserType.GeneralUser)
                .roles(Collections.singletonList("ROLE_USER")).build();
    }
    private UserPlaylist getUserPlaylist(UserEntity userEntity) {
        return UserPlaylist.builder()
                .userEntity(userEntity)
                .alarmSetDay("01")
                .alarmStartTime("01")
                .alarmEndTime("01")
                .build();
    }

    @Test
    public void 마이플레이리스트조회_0(){
        // given

        // when
        List<PlaylistResponse> result = mainRepository.findAllByUserEntity_Email("email@naver.com");

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 마이플레이리스트조회_1() {
        // given
        final UserEntity userEntity = getUserEntity();
        final UserPlaylist playlist = getUserPlaylist(userEntity);

        // when
        mainRepository.save(playlist);
        List<PlaylistResponse> result = mainRepository.findAllByUserEntity_Email(userEntity.getEmail());

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void 마이플레이리스트등록() {
        // given
        final UserEntity userEntity = getUserEntity();
        final UserPlaylist playlist = getUserPlaylist(userEntity);

        // when
        final UserPlaylist result = mainRepository.save(playlist);

        // then
        assertThat(result.getPlayKey()).isNotNull();
        assertThat(result.getAlarmSetDay().equals("01"));
        assertThat(result.getAlarmStartTime().equals("01"));
        assertThat(result.getAlarmEndTime().equals("01"));
    }
}
