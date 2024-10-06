package com.studyapp.fileservice.dao;

import com.studyapp.fileservice.entity.Media;
import org.springframework.data.repository.CrudRepository;

public interface MediaDao extends CrudRepository<Media, Long> {
}
