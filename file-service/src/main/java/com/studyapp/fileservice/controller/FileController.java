package com.studyapp.fileservice.controller;

import com.studyapp.fileservice.entity.Media;
import com.studyapp.fileservice.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<List<Media>> uploadFiles(@RequestPart("files") List<MultipartFile> files) {
        return ResponseEntity.ok(fileService.uploadFiles(files));
    }

    @PostMapping("/download")
    public ResponseEntity<Media> downloadFile(@RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.uploadFile(file));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFiles(@RequestParam List<Long> fileIds) {
        fileService.deleteFiles(fileIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bulk")
    public ResponseEntity<List<Media>> findMediaByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(fileService.findMediaByIds(ids));
    }

}
