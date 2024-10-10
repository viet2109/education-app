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
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
            String fileUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + fileName + "?alt=media";
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

    public void deleteFiles(List<Long> fileIds) {
        List<Media> mediaList = Streamable.of(mediaDao.findAllById(fileIds)).toList();

        if (mediaList.isEmpty()) {
            log.warn("No files found with given IDs: {}", fileIds);
            return;
        }

        Bucket bucket = StorageClient.getInstance().bucket();

        mediaList.forEach(media -> {
            try {
                Blob blob = bucket.get(media.getFilename());
                if (blob != null) {
                    boolean deleted = blob.delete();
                    if (deleted) {
                        log.info("Deleted file: {}", media.getFilename());
                        mediaDao.delete(media);
                    } else {
                        log.error("Failed to delete file from storage: {}", media.getFilename());
                    }
                } else {
                    log.warn("File not found in storage: {}", media.getFilename());
                }
            } catch (Exception e) {
                log.error("Failed to delete file: {}", media.getFilename(), e);
            }
        });
    }

}

