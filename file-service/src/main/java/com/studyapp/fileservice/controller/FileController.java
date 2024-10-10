package com.studyapp.fileservice.controller;

import com.studyapp.fileservice.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(@RequestPart("files") List<MultipartFile> files) throws IOException {
        return ResponseEntity.ok(fileService.uploadFiles(files));
    }

    @PostMapping("/download")
    public ResponseEntity<String> downloadFile(@RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.uploadFile(file));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFiles(@RequestParam List<Long> fileIds) {
        fileService.deleteFiles(fileIds);
        return ResponseEntity.ok().build();
    }

}
