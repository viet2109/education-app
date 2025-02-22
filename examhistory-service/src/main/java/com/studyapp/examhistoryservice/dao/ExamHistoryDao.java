package com.studyapp.examhistoryservice.dao;

import com.studyapp.examhistoryservice.entity.ExamHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamHistoryDao extends CrudRepository<ExamHistory, Long>, JpaSpecificationExecutor<ExamHistory> {
}
