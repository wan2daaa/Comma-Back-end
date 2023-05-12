package com.team.comma.util.s3.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.util.s3.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class FileUploadController {
    final private FileUploadService fileUploadService;

    @PostMapping("/resources")
    public ResponseEntity<MessageResponse> uploadFileToS3(@RequestParam("file") MultipartFile multipartFile)
            throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUploadService.uploadFileToS3(multipartFile));
    }

}
