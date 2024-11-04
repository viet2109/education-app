package com.studyapp.quizservice.services.QuizImportStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyapp.quizservice.client.file.FileClient;
import com.studyapp.quizservice.client.question.QuestionClient;
import com.studyapp.quizservice.client.question.dto.request.AnswerRequestFeignDto;
import com.studyapp.quizservice.client.question.dto.request.QuestionRequestFeignDto;
import com.studyapp.quizservice.dto.request.QuizRequestDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import com.studyapp.quizservice.services.QuizService;
import com.studyapp.quizservice.utils.CustomMultipartFile;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WordQuizImportStrategy implements QuizImportStrategy {
    private static final Logger log = LoggerFactory.getLogger(WordQuizImportStrategy.class);
    private final FileClient fileClient;
    private final QuizService quizService;
    private final QuestionClient questionClient;

    @Override
    public QuizResponseDto convertFileToDto(MultipartFile multipartFile, String userId) {
        String keyQuizStart = "QZ:";
        String keyQuestionStart = "QT:";
        String keyAnswerStart = "A:";
        String keyCorrectAnswer = "|";
        String questionMedia = "q";
        String answerMedia = "a";

        try (XWPFDocument document = new XWPFDocument(multipartFile.getInputStream())) {
            Long examId = null;
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            List<QuestionRequestFeignDto> questionRequestDtos = new ArrayList<>();

            List<MultipartFile> questionFiles = new ArrayList<>();
            List<MultipartFile> answerFiles = new ArrayList<>();
            int questionFileIndex = 0;
            int answerFileIndex = 0;

            paragraph:
            for (XWPFParagraph paragraph : paragraphs) {
                if (paragraph.getText().trim().isEmpty()) continue;
                StringBuilder text = new StringBuilder();
                String typeMediaList = "";
                int indexAnswer = -1;
                QuestionRequestFeignDto questionRequestDto = QuestionRequestFeignDto
                        .builder()
                        .filesIndex(new ArrayList<>())
                        .listAnswer(new ArrayList<>())
                        .build();
                for (XWPFRun run : paragraph.getRuns()) {
                    String subText = run.getText(0);
                    if (subText != null) {
                        text.append(subText);
                    }
                    //start new line
                    else {
                        if (text.toString().startsWith(keyQuizStart) && examId == null) {
                            String title = text.substring(keyQuizStart.length()).trim();
                            QuizRequestDto quizRequestDto = QuizRequestDto
                                    .builder()
                                    .title(title)
                                    .createdBy(userId)
                                    .build();
                            examId = quizService.createExam(quizRequestDto).getId();
                            continue paragraph;
                        } else if (text.toString().startsWith(keyQuestionStart)) {
                            String content = text.substring(keyQuestionStart.length()).trim();
                            questionRequestDto.setContent(content);
                            typeMediaList = questionMedia;
                            if (examId != null) {
                                questionRequestDto.setExamId(examId);
                            }
                        } else if (text.toString().startsWith(keyAnswerStart)) {
                            String content = text.substring(keyAnswerStart.length(), text.lastIndexOf(keyCorrectAnswer)).trim();
                            boolean isCorrect = Boolean.parseBoolean(text.substring(text.lastIndexOf(keyCorrectAnswer) + 1).trim());
                            typeMediaList = answerMedia;
                            indexAnswer++;
                            AnswerRequestFeignDto answerRequestDto = AnswerRequestFeignDto
                                    .builder()
                                    .isCorrect(isCorrect)
                                    .content(content)
                                    .filesIndex(new ArrayList<>())
                                    .build();
                            questionRequestDto.getListAnswer().add(answerRequestDto);
                        }
                        text.setLength(0);
                    }

                    for (XWPFPicture picture : run.getEmbeddedPictures()) {
                        XWPFPictureData pictureData = picture.getPictureData();
                        MultipartFile file = new CustomMultipartFile(
                                pictureData.getFileName(),
                                pictureData.getFileName(),
                                "image/" + getFileExtension(pictureData),
                                pictureData.getData()
                        );
                        if (typeMediaList.equals(questionMedia)) {
                            questionFiles.add(file);
                            questionRequestDto.getFilesIndex().add((long) questionFileIndex++);
                        } else if (typeMediaList.equals(answerMedia)) {
                            answerFiles.add(file);
                            questionRequestDto.getListAnswer().get(indexAnswer).getFilesIndex().add((long) answerFileIndex++);
                        }
                        fileClient.uploadFiles(Collections.singletonList(file));
                    }
                }
                questionRequestDtos.add(questionRequestDto);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String questionRequestFeignDtoListJson = objectMapper.writeValueAsString(questionRequestDtos);
            questionClient.createListQuestionByFeign(questionRequestFeignDtoListJson, questionFiles, answerFiles);
            return quizService.getExamById(examId);

        } catch (IOException exception) {
            log.error("Error when import file: {}", exception.getMessage());
        }
        return null;
    }

    private String getFileExtension(XWPFPictureData xwpfPictureData) {
        if (xwpfPictureData.suggestFileExtension() != null) return xwpfPictureData.suggestFileExtension();
        return xwpfPictureData.getFileName().substring(xwpfPictureData.getFileName().lastIndexOf('.') + 1);
    }


}
