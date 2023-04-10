package com.team.comma.controller;

import com.google.gson.Gson;
import com.team.comma.dto.PlaylistResponse;
import com.team.comma.service.MainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MainControllerTest {
    @InjectMocks
    private MainController target;
    @Mock
    private MainService mainService;
    private MockMvc mockMvc;
    private Gson gson;
    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .build(); // mockmvc가 null이 아니도록
    }

    final String userEmail = "email@naver.com";
    @Test
    public void 플레이리스트조회실패() throws Exception {
        // given
        final String url = "/main/playlist";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 플레이리스트조회성공() throws Exception {
        // given
        final String url = "/main/playlist";
        doReturn(Arrays.asList(
                PlaylistResponse.builder().build(),
                PlaylistResponse.builder().build(),
                PlaylistResponse.builder().build()
        )).when(mainService).getUserPlaylist(userEmail);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(userEmail)
        );

        // then
        resultActions.andExpect(status().isOk());
    }
}
