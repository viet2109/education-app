package com.studyapp.quizservice.services;

import com.studyapp.quizservice.client.question.QuestionClient;
import com.studyapp.quizservice.client.question.dto.response.QuestionResponseDto;
import com.studyapp.quizservice.client.user.UserClient;
import com.studyapp.quizservice.dao.QuizDao;
import com.studyapp.quizservice.dto.request.QuizRequestDto;
import com.studyapp.quizservice.dto.response.CategoryDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import com.studyapp.quizservice.entities.QuizEntity;
import com.studyapp.quizservice.enums.Category;
import com.studyapp.quizservice.enums.QuizError;
import com.studyapp.quizservice.exception.QuizException;
import com.studyapp.quizservice.mapper.QuizMapper;
import com.studyapp.quizservice.specification.QuizSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizService {

    QuizDao quizDao;
    QuestionClient questionClient;
    UserClient userClient;
    QuizMapper quizMapper;

    public List<CategoryDto> getAllCategories() {
        return Category.getAll();
    }

    public QuizResponseDto createExam(QuizRequestDto requestDto) {
        userClient.findUserById(requestDto.getCreatedBy());
        return quizMapper.entityToRpDto(quizDao.save(quizMapper.rqDtoToEntity(requestDto)));
    }

    public QuizResponseDto getExamById(Long id) {
        QuizResponseDto quizResponseDto = quizMapper.entityToRpDto(quizDao.findById(id).orElseThrow(() -> new QuizException(QuizError.EXAM_NOT_FOUND)));

        List<QuestionResponseDto> allQuestions = questionClient.getQuestionsByQuery(Collections.singletonList(id)).getBody();
        if (allQuestions == null) {
            allQuestions = Collections.emptyList();
        }

        quizResponseDto.setListQuestion(allQuestions);

        return quizResponseDto;
    }

    public void deleteQuizById(Long id) {
        quizDao.deleteById(id);
        questionClient.deleteQuestionByIdsOrExamId(null, id);
    }

    public Page<QuizResponseDto> getQuizzesByQuery(String title, List<String> categoryList, String createdBy,
                                                   Integer minDuration, Integer maxDuration,
                                                   LocalDateTime expiratedAtAfter, LocalDateTime expiratedAtBefore,
                                                   Pageable pageable) {
        Specification<QuizEntity> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and(QuizSpecification.hasTitle(title));
        }

        if (categoryList != null && !categoryList.isEmpty()) {
            List<Category> categories = categoryList.stream()
                    .map(String::toUpperCase)
                    .map(catStr -> {
                        try {
                            return Category.valueOf(catStr);
                        } catch (IllegalArgumentException e) {
                            // Log or handle invalid categories (in this case, ignoring invalid categories)
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!categories.isEmpty()) {
                spec = spec.and(QuizSpecification.hasAnyCategory(categories));
            }
        }

        if (createdBy != null && !createdBy.isEmpty()) {
            spec = spec.and(QuizSpecification.createdBy(createdBy));
        }

        if (minDuration != null) {
            spec = spec.and(QuizSpecification.hasDurationGreaterThanOrEqual(minDuration));
        }

        if (maxDuration != null) {
            spec = spec.and(QuizSpecification.hasDurationLessThanOrEqual(maxDuration));
        }

        if (expiratedAtAfter != null) {
            spec = spec.and(QuizSpecification.expiratedAtAfter(expiratedAtAfter));
        }

        if (expiratedAtBefore != null) {
            spec = spec.and(QuizSpecification.expiratedAtBefore(expiratedAtBefore));
        }

        return quizDao.findAll(spec, pageable).map(quizMapper::entityToRpDto);
    }

}
