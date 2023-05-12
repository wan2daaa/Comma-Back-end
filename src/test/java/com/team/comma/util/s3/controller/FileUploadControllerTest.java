package com.team.comma.util.s3.controller;

import com.google.gson.Gson;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.util.gson.GsonUtil;
import com.team.comma.util.s3.exception.S3Exception;
import com.team.comma.util.s3.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static com.team.comma.common.constant.ResponseCodeEnum.SIMPLE_REQUEST_FAILURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(FileUploadController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class FileUploadControllerTest {
    @MockBean
    private FileUploadService fileUploadService;
    MockMvc mockMvc;
    Gson gson;

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
                     RestDocumentationContextProvider restDocumentation) {
        gson = GsonUtil.getGsonInstance();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("파일 업로드 실패 _ 이미지 파일이 아님")
    public void uploadFileFail_notImageFile() throws Exception {
        // given
        final String api = "/s3/resources";
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = new byte[1];
        MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

        doThrow(new S3Exception("이미지 파일만 업로드할 수 있습니다.")).when(fileUploadService).uploadFileToS3(any(MultipartFile.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                multipart(api).file("file", multipartFile.getBytes()));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("upload/fail-notImage",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                        requestParts(partWithName("file").description("이미지 파일 데이터")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("사용자 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("파일 업로드 실패 _ 이미지 파일이 아님")
    public void uploadFileSuccess() throws Exception {
        // given
        final String api = "/s3/resources";
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = new byte[1];
        MultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);

        doReturn(MessageResponse.of(REQUEST_SUCCESS, "url")).when(fileUploadService).uploadFileToS3(any(MultipartFile.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                multipart(api).file("file", multipartFile.getBytes()));

        // then
        resultActions.andExpect(status().isCreated()).andDo(
                document("upload/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(partWithName("file").description("이미지 파일 데이터")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("S3 이미지 파일 주소")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo("url");
    }
}
