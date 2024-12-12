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
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {
    MediaDao mediaDao;

    public List<Media> uploadFiles(List<MultipartFile> files) {
        // Loại bỏ các file null hoặc trùng lặp trong danh sách
        List<MultipartFile> distinctFiles = files.stream()
                .filter(file -> file != null && !file.isEmpty()) // Loại bỏ file null hoặc rỗng
                .distinct() // Loại bỏ các file trùng lặp
                .toList();
        log.info("files size: {}", distinctFiles.size());
        // Sử dụng ExecutorService để quản lý song song hóa
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            // Sử dụng CompletableFuture để xử lý song song upload
            List<CompletableFuture<Media>> uploadFutures = distinctFiles.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return uploadFile(file);
                        } catch (IOException e) {
                            throw new CompletionException(new IOException("Error uploading file: " + file.getOriginalFilename(), e));
                        }
                    }, executorService))
                    .toList();

            // Chờ tất cả các tác vụ hoàn thành và thu thập kết quả
            return uploadFutures.stream()
                    .map(CompletableFuture::join) // Ghi log lỗi nếu cần xử lý riêng
                    .toList();
        } finally {
            // Đảm bảo ExecutorService được tắt sau khi hoàn tất
            executorService.shutdown();
        }
    }

    public Media uploadFile(MultipartFile file) throws IOException {
        // Kiểm tra nếu file rỗng và ném ngoại lệ
        if (file.isEmpty()) {
            throw new IOException("Cannot upload an empty file: " + file.getOriginalFilename());
        }

        // Tạo tên file duy nhất
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String fileType = file.getContentType();
        long fileSize = file.getSize();
        Bucket bucket = StorageClient.getInstance().bucket();

        // Upload file bằng InputStream để tránh lỗi tràn bộ nhớ
        try (InputStream inputStream = file.getInputStream()) {
            Blob blob = bucket.create(fileName, inputStream, fileType);
            String fileUrl = "https://firebasestorage.googleapis.com/v0/b/" + bucket.getName() + "/o/" + fileName + "?alt=media";

            // Tạo đối tượng Media để lưu thông tin file
            Media media = Media.builder()
                    .filename(fileName)
                    .fileType(fileType)
                    .sizeInBytes(fileSize)
                    .fileUrl(fileUrl)
                    .build();

            // Lưu vào cơ sở dữ liệu
            Media mediaSaved = mediaDao.save(media);
            log.info("Created file: {}", blob.getMediaLink());
            return mediaSaved;
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

    public List<Media> findMediaByIds(List<Long> ids) {
        return Streamable.of(mediaDao.findAllById(ids)).toList();
    }

}

