package com.team.comma.util.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.util.s3.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public MessageResponse uploadFileToS3(MultipartFile file) throws IOException {
        String uuid = UUID.randomUUID().toString();

        if(!isImageFile(file)) {
            throw new S3Exception("이미지 파일만 업로드할 수 있습니다.");
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getInputStream().available());
        amazonS3Client.putObject(bucket , uuid ,file.getInputStream() , metadata);

        return MessageResponse.of(REQUEST_SUCCESS , amazonS3Client.getUrl(bucket , uuid));
    }

    public boolean isImageFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        if(extension.equals("jpg") || extension.equals("png")) {
            return true;
        }

        return false;
    }

}
