package com.studyapp.quizservice.services;

import com.studyapp.quizservice.client.question.QuestionClient;
import com.studyapp.quizservice.client.question.dto.response.QuestionResponseDto;
import com.studyapp.quizservice.client.user.UserClient;
import com.studyapp.quizservice.dao.QuizDao;
import com.studyapp.quizservice.dto.request.QuizRequestDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import com.studyapp.quizservice.entities.QuizEntity;
import com.studyapp.quizservice.enums.Category;
import com.studyapp.quizservice.enums.QuizError;
import com.studyapp.quizservice.exception.QuizException;
import com.studyapp.quizservice.mapper.QuizMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizService {

    QuizDao quizDao;
    QuestionClient questionClient;
    UserClient userClient;
    QuizMapper quizMapper;

    public List<Category> getAllCategory() {
        return Streamable.of(quizDao.findAll()).toList().stream().map(QuizEntity::getCategory).toList();
    }

    public List<QuizResponseDto> getAllExam() {
        // Lấy danh sách bài thi và chuyển đổi sang DTO
        List<QuizResponseDto> responseDto = Streamable.of(quizDao.findAll()).toList().stream()
                .map(quizMapper::entityToRpDto).toList();

        // Lấy danh sách ID của các bài thi
        List<Long> quizIds = responseDto.stream().map(QuizResponseDto::getId).toList();

        // Gọi Feign Client một lần với tất cả các ID bài thi nếu danh sách không rỗng
        List<QuestionResponseDto> allQuestions = quizIds.isEmpty() ? Collections.emptyList()
                : questionClient.getQuestionsByQuery(null, quizIds).getBody();

        if (allQuestions == null) {
            allQuestions = Collections.emptyList();
        }

        // Gán câu hỏi vào từng bài thi tương ứng
        Map<Long, List<QuestionResponseDto>> questionsByExamId = allQuestions.stream()
                .collect(Collectors.groupingBy(QuestionResponseDto::getExamId));

        responseDto.forEach(quiz -> quiz.setListQuestion(
                questionsByExamId.getOrDefault(quiz.getId(), Collections.emptyList())));

        return responseDto;
    }

    public QuizResponseDto createExam(QuizRequestDto requestDto) {
        userClient.findUserById(requestDto.getCreatedBy());
        return quizMapper.entityToRpDto(quizDao.save(quizMapper.rqDtoToEntity(requestDto)));
    }

    public QuizResponseDto getExamById(Long id) {
        QuizResponseDto quizResponseDto = quizMapper.entityToRpDto(quizDao.findById(id).orElseThrow(() -> new QuizException(QuizError.EXAM_NOT_FOUND)));

        List<QuestionResponseDto> allQuestions = questionClient.getQuestionsByQuery(null, Collections.singletonList(id)).getBody();

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
}
