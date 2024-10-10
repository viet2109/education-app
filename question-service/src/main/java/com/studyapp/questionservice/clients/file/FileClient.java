package com.studyapp.questionservice.clients.file;

import com.studyapp.questionservice.config.FeignConfig;
import com.studyapp.questionservice.config.FeignMultipartConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(value = "file-service", path = "/files", configuration = {FeignMultipartConfig.class, FeignConfig.class})
public interface FileClient {
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<List<String>> updateFiles(@RequestPart("files") List<MultipartFile> files);
}
