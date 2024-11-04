package com.studyapp.questionservice.services;

import com.studyapp.questionservice.clients.file.FileClient;
import com.studyapp.questionservice.clients.file.dto.Media;
import com.studyapp.questionservice.clients.quiz.QuizClient;
import com.studyapp.questionservice.dao.QuestionDao;
import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.dto.response.AnswerResponseDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.entities.AnswerEntity;
import com.studyapp.questionservice.entities.QuestionEntity;
import com.studyapp.questionservice.enums.QuestionError;
import com.studyapp.questionservice.exceptions.QuestionException;
import com.studyapp.questionservice.mapper.AnswerMapper;
import com.studyapp.questionservice.mapper.QuestionMapper;
import com.studyapp.questionservice.specification.QuestionSpecification;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionService {
    QuestionDao questionDao;
    QuestionMapper questionMapper;
    AnswerMapper answerMapper;
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
            List<Media> questionUrls = Optional.ofNullable(questionDto.getFiles())
                    .filter(files -> !files.isEmpty()) // Kiểm tra files có null hoặc rỗng không
                    .map(fileClient::uploadFiles) // Chỉ gọi fileClient nếu có files
                    .map(ResponseEntity::getBody)
                    .orElse(new ArrayList<>());

            // Tạo entity câu hỏi
            QuestionEntity questionEntity = QuestionEntity.builder()
                    .content(questionDto.getContent())
                    .examId(questionDto.getExamId())
                    .fileIds(questionUrls.stream().map(Media::getId).toList())
                    .build();

            // Xử lý danh sách câu trả lời
            List<AnswerEntity> answerEntityList = questionDto.getListAnswer().stream().map(answerDto -> {
                // Kiểm tra và upload file liên quan đến câu trả lời nếu có
                List<Media> answerUrls = Optional.ofNullable(answerDto.getFiles())
                        .filter(files -> !files.isEmpty()) // Kiểm tra files có null hoặc rỗng không
                        .map(fileClient::uploadFiles) // Chỉ gọi fileClient nếu có files
                        .map(ResponseEntity::getBody)
                        .orElse(new ArrayList<>());

                return AnswerEntity.builder()
                        .content(answerDto.getContent())
                        .isCorrect(answerDto.getIsCorrect())
                        .question(questionEntity)
                        .fileIds(answerUrls.stream().map(Media::getId).toList())
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

    public List<QuestionResponseDto> getQuestionsByQuery(List<Long> examIds) {
        Specification<QuestionEntity> spec = Specification.where(null);

        if (examIds != null && !examIds.isEmpty()) {
            for (Long examId : examIds) {
                spec = spec.and(QuestionSpecification.hasExamId(examId));
            }
        }

        return questionDao.findAll(spec).stream().map(entity -> {
            QuestionResponseDto responseDto = questionMapper.entityToRpDto(entity);
            responseDto.setFiles(fileClient.findMediaByIds(entity.getFileIds()).getBody());

            // Ánh xạ danh sách câu trả lời từ thực thể sang DTO
            List<AnswerResponseDto> answerResponseDtos = entity.getListAnswer().stream()
                    .map(answerEntity -> {
                        // Chuyển đổi từng AnswerEntity sang AnswerResponseDto
                        AnswerResponseDto answerResponseDto = answerMapper.entityToRpDto(answerEntity);

                        // Lấy file của câu trả lời nếu có
                        if (answerEntity.getFileIds() != null) {
                            answerResponseDto.setFiles(fileClient.findMediaByIds(answerEntity.getFileIds()).getBody());
                        }

                        return answerResponseDto;
                    })
                    .toList();

            // Gán danh sách câu trả lời cho DTO phản hồi
            responseDto.setListAnswer(answerResponseDtos);
            return responseDto;
        }).toList();
    }

    public QuestionResponseDto getQuestionById(Long id) {
        QuestionEntity entity = questionDao.findById(id).orElseThrow(() -> new QuestionException(QuestionError.QUESTION_NOT_FOUND));
        QuestionResponseDto responseDto = questionMapper.entityToRpDto(entity);
        responseDto.setFiles(fileClient.findMediaByIds(entity.getFileIds()).getBody());

        // Ánh xạ danh sách câu trả lời từ thực thể sang DTO
        List<AnswerResponseDto> answerResponseDtos = entity.getListAnswer().stream()
                .map(answerEntity -> {
                    // Chuyển đổi từng AnswerEntity sang AnswerResponseDto
                    AnswerResponseDto answerResponseDto = answerMapper.entityToRpDto(answerEntity);

                    // Lấy file của câu trả lời nếu có
                    if (answerEntity.getFileIds() != null) {
                        answerResponseDto.setFiles(fileClient.findMediaByIds(answerEntity.getFileIds()).getBody());
                    }

                    return answerResponseDto;
                })
                .toList();

        // Gán danh sách câu trả lời cho DTO phản hồi
        responseDto.setListAnswer(answerResponseDtos);
        return responseDto;

    }

    @Transactional
    public void deleteQuestionsByIdsOrExamId(List<Long> ids, Long examId) {
        if (ids != null) {
            questionDao.deleteAllById(ids);
        }
        if (examId != null) {
            List<QuestionEntity> questions = questionDao.findByExamId(examId);
            questionDao.deleteAll(questions);
        }
    }

}
