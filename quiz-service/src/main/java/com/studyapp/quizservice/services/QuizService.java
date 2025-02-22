package com.studyapp.quizservice.services;

import com.studyapp.quizservice.client.examHistory.ExamHistoryClient;
import com.studyapp.quizservice.client.examHistory.dto.ExamHistoryDetailDto;
import com.studyapp.quizservice.client.examHistory.dto.request.ExamHistoryRequestDto;
import com.studyapp.quizservice.client.examHistory.dto.response.ExamHistoryResponseDto;
import com.studyapp.quizservice.client.question.QuestionClient;
import com.studyapp.quizservice.client.question.dto.response.AnswerChangeResponseDto;
import com.studyapp.quizservice.client.question.dto.response.QuestionChangeResponseDto;
import com.studyapp.quizservice.client.question.dto.response.QuestionResponseDto;
import com.studyapp.quizservice.client.user.UserClient;
import com.studyapp.quizservice.dao.QuizDao;
import com.studyapp.quizservice.dto.request.QuizAnswerDto;
import com.studyapp.quizservice.dto.request.QuizRequestDto;
import com.studyapp.quizservice.dto.response.CategoryDto;
import com.studyapp.quizservice.dto.response.QuizChangeResponseDto;
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
    ExamHistoryClient examHistoryClient;

    public List<CategoryDto> getAllCategories() {
        return Category.getAll();
    }

    public ExamHistoryResponseDto calculateScore(Long quizId, QuizAnswerDto quizAnswerDto) {

        // Lấy danh sách câu hỏi từ QuestionService
        List<QuestionChangeResponseDto> questionChangeResponseDtos = questionClient
                .getQuestionsManageByQuery(Collections.singletonList(quizId))
                .getBody();

        if (questionChangeResponseDtos == null || questionChangeResponseDtos.isEmpty()) {
            throw new IllegalArgumentException("No questions found for the given quizId.");
        }

        // Danh sách các đáp án đúng
        Set<Long> correctAnswers = questionChangeResponseDtos.stream()
                .flatMap(dto -> dto.getListAnswer().stream())
                .filter(AnswerChangeResponseDto::getIsCorrect)
                .map(AnswerChangeResponseDto::getId)
                .collect(Collectors.toSet());

        // Tính điểm mỗi câu hỏi
        final double maxScore = 10.0;
        final double pointPerQuestion = maxScore / questionChangeResponseDtos.size();

        // Tính tổng điểm của người dùng
        double totalScore = questionChangeResponseDtos.stream()
                .mapToDouble(question -> calculateQuestionScore(
                        question,
                        quizAnswerDto.getUserAnswers().getOrDefault(question.getId(), Collections.emptyList()),
                        pointPerQuestion
                ))
                .sum();

        // Làm tròn điểm
        double roundedScore = Math.round(totalScore * 100.0) / 100.0;

        // Xây dựng danh sách chi tiết lịch sử bài thi
        List<ExamHistoryDetailDto> examHistoryDetails = quizAnswerDto.getUserAnswers().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(answerId ->
                        ExamHistoryDetailDto.builder()
                                .questionId(entry.getKey())
                                .answerId(answerId)
                                .isCorrect(correctAnswers.contains(answerId))
                                .build()
                ))
                .collect(Collectors.toList());

        // Tạo ExamHistoryRequestDto
        ExamHistoryRequestDto examHistoryRequestDto = ExamHistoryRequestDto.builder()
                .startedAt(quizAnswerDto.getStartedAt())
                .finishedAt(quizAnswerDto.getFinishedAt())
                .score(roundedScore)
                .examHistoryDetail(examHistoryDetails)
                .examId(quizId)
                .userId(quizAnswerDto.getUserId())
                .build();

        // Gửi dữ liệu tới ExamHistoryClient và trả về kết quả
        return examHistoryClient.createExamHistory(examHistoryRequestDto).getBody();
    }

    private double calculateQuestionScore(QuestionChangeResponseDto question, List<Long> userAnswers, double pointPerQuestion) {
        List<Long> correctAnswers = question.getListAnswer().stream()
                .filter(AnswerChangeResponseDto::getIsCorrect)
                .map(AnswerChangeResponseDto::getId)
                .toList();

        if (correctAnswers.isEmpty()) {
            return 0; // Câu hỏi không có đáp án đúng
        }

        double pointPerCorrectAnswer = pointPerQuestion / correctAnswers.size();
        return userAnswers.stream()
                .filter(correctAnswers::contains)
                .mapToDouble(answer -> pointPerCorrectAnswer)
                .sum();
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

    public QuizChangeResponseDto getExamManageById(Long id) {
        QuizChangeResponseDto quizResponseDto = quizMapper.entityToChangeRpDto(quizDao.findById(id).orElseThrow(() -> new QuizException(QuizError.EXAM_NOT_FOUND)));

        List<QuestionChangeResponseDto> allQuestions = questionClient.getQuestionsManageByQuery(Collections.singletonList(id)).getBody();
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

    public Page<QuizResponseDto> getQuizzesByQuery(String title, List<String> categoryList, String createdBy, Integer minDuration, Integer maxDuration, LocalDateTime expiratedAtAfter, LocalDateTime expiratedAtBefore, Pageable pageable) {
        Specification<QuizEntity> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and(QuizSpecification.hasTitle(title));
        }

        if (categoryList != null && !categoryList.isEmpty()) {
            List<Category> categories = categoryList.stream().map(String::toUpperCase).map(catStr -> {
                try {
                    return Category.valueOf(catStr);
                } catch (IllegalArgumentException e) {
                    // Log or handle invalid categories (in this case, ignoring invalid categories)
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());

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

    public List<QuizResponseDto> getQuizzesByQueryNoPagination(String title, List<String> categoryList, String createdBy, Integer minDuration, Integer maxDuration, LocalDateTime expiratedAtAfter, LocalDateTime expiratedAtBefore) {
        Specification<QuizEntity> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and(QuizSpecification.hasTitle(title));
        }

        if (categoryList != null && !categoryList.isEmpty()) {
            List<Category> categories = categoryList.stream().map(String::toUpperCase).map(catStr -> {
                try {
                    return Category.valueOf(catStr);
                } catch (IllegalArgumentException e) {
                    // Log hoặc xử lý các category không hợp lệ (trong trường hợp này bỏ qua)
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());

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

        return quizDao.findAll(spec).stream().map(quizMapper::entityToRpDto).collect(Collectors.toList());
    }


    public Page<QuizChangeResponseDto> getQuizzesManageByQuery(String title, List<String> categoryList, String createdBy, Integer minDuration, Integer maxDuration, LocalDateTime expiratedAtAfter, LocalDateTime expiratedAtBefore, Pageable pageable) {
        Specification<QuizEntity> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and(QuizSpecification.hasTitle(title));
        }

        if (categoryList != null && !categoryList.isEmpty()) {
            List<Category> categories = categoryList.stream().map(String::toUpperCase).map(catStr -> {
                try {
                    return Category.valueOf(catStr);
                } catch (IllegalArgumentException e) {
                    // Log or handle invalid categories (in this case, ignoring invalid categories)
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());

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

        return quizDao.findAll(spec, pageable).map(quizMapper::entityToChangeRpDto);
    }

}
