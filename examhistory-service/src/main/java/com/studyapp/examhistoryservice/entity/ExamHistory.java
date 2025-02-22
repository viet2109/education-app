package com.studyapp.examhistoryservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    LocalDateTime startedAt;

    @Column(nullable = false)
    LocalDateTime finishedAt;

    @Column(nullable = false)
    Double score;

    @Column(nullable = false)
    String userId;

    @Column(nullable = false)
    Long examId;

    @OneToMany(mappedBy = "examHistory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<ExamHistoryDetail> examHistoryDetail;
}
