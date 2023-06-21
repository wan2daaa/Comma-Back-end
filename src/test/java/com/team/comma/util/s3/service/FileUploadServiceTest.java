package com.team.comma.util.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.util.s3.exception.S3Exception;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class FileUploadServiceTest {
    @InjectMocks
    private FileUploadService fileUploadService;

    @Mock
    private AmazonS3Client amazonS3Client;

    @Test
    @DisplayName("파일 업로드 실패 _ 이미지 파일이 아님")
    public void uploadFileFail_notImageFile() {
        // given
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = new byte[1];
        MultipartFile result = new MockMultipartFile(name,originalFileName, contentType, content);

        // when
        Throwable thrown = catchThrowable(() -> fileUploadService.uploadFileToS3(result));

        // then
        assertThat(thrown).isInstanceOf(S3Exception.class).hasMessage("이미지 파일만 업로드할 수 있습니다.");
    }

    @Test
    @DisplayName("파일 업로드 성공")
    public void uploadFileSuccess() throws IOException {
        // given
        String name = "file.jpg";
        String originalFileName = "file.jpg";
        String contentType = "image/jpeg";
        byte[] content = new byte[1];
        MultipartFile multipartFile = new MockMultipartFile(name,originalFileName, contentType, content);

        doReturn(null).when(amazonS3Client).putObject(any() , any(String.class) , any(InputStream.class) , any(ObjectMetadata.class));
        doReturn(null).when(amazonS3Client).getUrl(any() , any(String.class));

        // when
        MessageResponse result = fileUploadService.uploadFileToS3(multipartFile);

        // then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo("요청이 성공적으로 수행되었습니다.");
        assertThat(result.getData()).isEqualTo(null);
    }
}
