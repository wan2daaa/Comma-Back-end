package com.team.comma.repository;

import com.team.comma.entity.UserEntity;
import com.team.comma.entity.UserPlaylist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;  //자동 import되지 않음

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MainRepositoryTest {

    @Autowired
    private MainRepository mainRepository;

    @Test
    public void 마이플레이리스트조회_0(){
        // given

        // when
        List<UserPlaylist> result = mainRepository.findAllByUserInfo_UserKey(1234);

        // then

        assertThat(result.size()).isEqualTo(0);
    }
}
