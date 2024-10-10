package com.studyapp.questionservice.services;

import com.studyapp.questionservice.clients.file.FileClient;
import com.studyapp.questionservice.clients.quiz.QuizClient;
import com.studyapp.questionservice.dao.QuestionDao;
import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.entities.AnswerEntity;
import com.studyapp.questionservice.entities.QuestionEntity;
import com.studyapp.questionservice.enums.QuestionError;
import com.studyapp.questionservice.exceptions.QuestionException;
import com.studyapp.questionservice.mapper.QuestionMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionService {
    QuestionDao questionDao;
    QuestionMapper questionMapper;
    QuizClient quizClient;
    FileClient fileClient;

    @Transactional
    public List<QuestionResponseDto> createListQuestion(List<QuestionRequestDto> listDto) {
        // Kiểm tra và preload examId
        List<Long> examIds = listDto.stream()
                .map(QuestionRequestDto::getExamId)
                .distinct()
                .toList();
        examIds.forEach(quizClient::getExamById); // Kiểm tra tính hợp lệ của examId

        // Tạo câu hỏi và câu trả lời
        List<QuestionEntity> listSavedQuestion = listDto.stream().map(questionDto -> {
            // Kiểm tra và upload file liên quan đến câu hỏi nếu có
            List<String> questionUrls = Optional.ofNullable(questionDto.getFiles())
                    .filter(files -> !files.isEmpty()) // Kiểm tra files có null hoặc rỗng không
                    .map(fileClient::updateFiles) // Chỉ gọi fileClient nếu có files
                    .map(ResponseEntity::getBody)
                    .orElse(new ArrayList<>());

            // Tạo entity câu hỏi
            QuestionEntity questionEntity = QuestionEntity.builder()
                    .content(questionDto.getContent())
                    .examId(questionDto.getExamId())
                    .filesUrl(questionUrls)
                    .build();

            // Xử lý danh sách câu trả lời
            List<AnswerEntity> answerEntityList = questionDto.getListAnswer().stream().map(answerDto -> {
                // Kiểm tra và upload file liên quan đến câu trả lời nếu có
                List<String> answerUrls = Optional.ofNullable(answerDto.getFiles())
                        .filter(files -> !files.isEmpty()) // Kiểm tra files có null hoặc rỗng không
                        .map(fileClient::updateFiles) // Chỉ gọi fileClient nếu có files
                        .map(ResponseEntity::getBody)
                        .orElse(new ArrayList<>());

                return AnswerEntity.builder()
                        .content(answerDto.getContent())
                        .isCorrect(answerDto.getIsCorrect())
                        .question(questionEntity)
                        .filesUrl(answerUrls)
                        .build();
            }).toList();

            // Gắn danh sách câu trả lời vào câu hỏi
            questionEntity.setListAnswer(answerEntityList);
            return questionEntity;
        }).toList();

        // Batch insert tất cả câu hỏi
        questionDao.saveAll(listSavedQuestion);

        // Chuyển đổi kết quả sang DTO
        return listSavedQuestion.stream()
                .map(questionMapper::entityToRpDto)
                .toList();
    }

    @Transactional
    public QuestionResponseDto createQuestion(QuestionRequestDto questionDto) {
        return createListQuestion(Collections.singletonList(questionDto)).getFirst();
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        questionDao.deleteById(questionId);
    }

    public List<QuestionResponseDto> getQuestionsByQuery(List<Long> ids, List<Long> examIds) {
        return questionDao.findByQuery(ids, examIds).stream().map(questionMapper::entityToRpDto).toList();
    }

    public QuestionResponseDto getQuestionById(Long id) {
        return questionMapper.entityToRpDto(questionDao.findById(id).orElseThrow(() -> new QuestionException(QuestionError.QUESTION_NOT_FOUND)));
    }

    @Transactional
    public void deleteQuestionsByIdsOrExamId(List<Long> ids, Long examId) {
        questionDao.deleteByIdsOrExamId(ids, examId);
    }

}
