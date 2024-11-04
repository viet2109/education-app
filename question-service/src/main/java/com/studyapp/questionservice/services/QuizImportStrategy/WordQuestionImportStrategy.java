package com.studyapp.questionservice.services.QuizImportStrategy;

import com.studyapp.questionservice.clients.file.FileClient;
import com.studyapp.questionservice.dto.request.AnswerRequestDto;
import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import com.studyapp.questionservice.utils.CustomMultipartFile;
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
public class WordQuestionImportStrategy implements QuestionImportStrategy {
    private static final Logger log = LoggerFactory.getLogger(WordQuestionImportStrategy.class);
    private FileClient fileClient;

    @Override
    public List<QuestionResponseDto> convertFileToDto(MultipartFile multipartFile, Long examId) {
        String keyQuizStart = "QZ:";
        String keyQuestionStart = "QT:";
        String keyAnswerStart = "A:";
        String keyCorrectAnswer = "|";
        String questionMedia = "q";
        String answerMedia = "a";

        try (XWPFDocument document = new XWPFDocument(multipartFile.getInputStream())) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            List<QuestionRequestDto> questionRequestDtos = new ArrayList<>();
            for (XWPFParagraph paragraph : paragraphs) {
                if (paragraph.getText().trim().isEmpty()) continue;
                StringBuilder text = new StringBuilder();
                String typeMediaList = "";
                int indexAnswer = -1;
                QuestionRequestDto questionRequestDto = QuestionRequestDto
                        .builder()
                        .files(new ArrayList<>())
                        .listAnswer(new ArrayList<>())
                        .examId(examId)
                        .build();
                for (XWPFRun run : paragraph.getRuns()) {
                    String subText = run.getText(0);
                    if (subText != null) {
                        text.append(subText);
                    }
                    //start new line
                    else {
                        if (text.toString().startsWith(keyQuizStart)) {
                            String title = text.substring(keyQuizStart.length()).trim();
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
                            AnswerRequestDto answerRequestDto = AnswerRequestDto
                                    .builder()
                                    .isCorrect(isCorrect)
                                    .content(content)
                                    .files(new ArrayList<>())
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
                            questionRequestDto.getFiles().add(file);
                        } else if (typeMediaList.equals(answerMedia)) {
                            questionRequestDto.getListAnswer().get(indexAnswer).getFiles().add(file);
                        }
                        fileClient.uploadFiles(Collections.singletonList(file));
                    }
                }
                questionRequestDtos.add(questionRequestDto);
            }
            return null;
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
