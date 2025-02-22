package com.studyapp.examhistoryservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
public class ExamHistoryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonIgnore
    private ExamHistory examHistory;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private Long answerId;

    @Column(nullable = false)
    private Boolean isCorrect;

}
