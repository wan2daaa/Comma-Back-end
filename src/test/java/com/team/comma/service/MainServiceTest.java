package com.team.comma.service;

import com.team.comma.dto.PlaylistResponse;
import com.team.comma.entity.UserPlaylist;
import com.team.comma.repository.MainRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.assertj.core.api.Assertions.assertThat;  //자동 import되지 않음

@ExtendWith(MockitoExtension.class)
public class MainServiceTest {

    @InjectMocks
    private MainService target;
    @Mock
    private MainRepository mainRepository;
    private final String email = "email@naver.com";

    @Test
    public void 플레이리스트목록조회() {
        // given
        doReturn(Arrays.asList(
                UserPlaylist.builder().build(),
                UserPlaylist.builder().build(),
                UserPlaylist.builder().build()
        )).when(mainRepository).findAllByUserEntity_Email(email);

        // when
        final List<PlaylistResponse> result = target.getUserPlaylist(email);

        // then
        assertThat(result.size()).isEqualTo(3);
    }
}
