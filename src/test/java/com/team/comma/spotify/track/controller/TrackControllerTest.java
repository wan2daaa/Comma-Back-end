package com.team.comma.spotify.track.controller;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_ENTITY_NOT_FOUND;
import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.track.service.TrackService;
import com.team.comma.util.gson.GsonUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(TrackController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
class TrackControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TrackService trackService;

    MockMvc mockMvc;
    Gson gson;

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();

        gson = GsonUtil.getGsonInstance();
    }


    @Test
    void 트랙의_알림상태_변경_성공() throws Exception {
        //given
        doReturn(
            MessageResponse.of(
                REQUEST_SUCCESS
            )
        ).when(trackService).updateAlarmFlag(anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(
            patch("/track/alarm/{trackId}", 1L)
                .contentType(APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
            .andDo(
                document(
                    "spotify/track/updateAlarmFlag/success",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("trackId").description("트랙 아이디")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").ignored()
                    )
                )
            );
    }
    
    @Test
    void 트랙의_알림상태_변경_실패__존재하지않는_트랙() throws Exception {
        //given
        doThrow(
            new EntityNotFoundException()
        ).when(trackService).updateAlarmFlag(anyLong());
        
        //when
        ResultActions resultActions = mockMvc.perform(
            patch("/track/alarm/{trackId}", 1L)
                .contentType(APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "spotify/track/updateAlarmFlag/fail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("trackId").description("트랙 아이디")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").ignored()
                    )
                )
            );
    }

}