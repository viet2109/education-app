package com.studyapp.fileservice.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.studyapp.fileservice.dao.MediaDao;
import com.studyapp.fileservice.entity.Media;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {
    MediaDao mediaDao;


    public List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        List<CompletableFuture<String>> uploadFutures = files.parallelStream()
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return uploadFile(file);
                    } catch (IOException e) {
                        return "Error uploading file " + file.getOriginalFilename() + ": " + e.getMessage();
                    }
                }))
                .toList();

        return uploadFutures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot upload an empty file: " + file.getOriginalFilename());
        }

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String typeFile = file.getContentType();
        long fileSize = file.getSize();
        Bucket bucket = StorageClient.getInstance().bucket();

        // Upload file using InputStream to avoid memory issues
        try (InputStream inputStream = file.getInputStream()) {
            Blob blob = bucket.create(fileName, inputStream, typeFile);
            String fileUrl = blob.getMediaLink();
            Media media = Media.builder()
                    .filename(fileName)
                    .fileType(typeFile)
                    .sizeInBytes(fileSize)
                    .fileUrl(fileUrl)
                    .build();
            mediaDao.save(media);
            log.info("Created file: {}", blob.getMediaLink());
            return fileUrl;
        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new IOException("Failed to upload file: " + file.getOriginalFilename(), e);
        }
    }
}

