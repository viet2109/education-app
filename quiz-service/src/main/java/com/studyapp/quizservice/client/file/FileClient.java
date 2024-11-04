package com.studyapp.quizservice.client.file;

import com.studyapp.quizservice.client.file.dto.Media;
import com.studyapp.quizservice.config.FeignConfig;
import com.studyapp.quizservice.config.FeignMultipartConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(value = "file-service", path = "/files", configuration = {FeignMultipartConfig.class, FeignConfig.class})
public interface FileClient {
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<List<Media>> uploadFiles(@RequestPart("files") List<MultipartFile> files);

    @GetMapping("/bulk")
    ResponseEntity<List<Media>> findMediaByIds(@RequestParam List<Long> ids);
}
