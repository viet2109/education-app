package com.studyapp.fileservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "media")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String filename;

    @Column(nullable = false)
    String fileUrl;

    @Column(nullable = false)
    String fileType;

    @Column(nullable = false)
    long sizeInBytes;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    LocalDateTime createdDate;


    @UpdateTimestamp
    LocalDateTime updatedDate;

    Long userId;
}

