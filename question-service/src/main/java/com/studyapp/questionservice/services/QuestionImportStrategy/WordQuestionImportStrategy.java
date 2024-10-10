package com.studyapp.questionservice.services.QuestionImportStrategy;

import com.studyapp.questionservice.dto.request.AnswerRequestDto;
import com.studyapp.questionservice.dto.request.QuestionRequestDto;
import com.studyapp.questionservice.utils.CustomMultipartFile;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class WordQuestionImportStrategy implements QuestionImportStrategy {
    private static final Logger log = LoggerFactory.getLogger(WordQuestionImportStrategy.class);

    @Override
    public List<QuestionRequestDto> convertFileToDto(MultipartFile multipartFile, Long examId) {

        List<QuestionRequestDto> questionRequestDtos = new ArrayList<>();
        String keyQuestionStart = "Q:";
        String keyAnswerStart = "A:";
        String keyMediaStart = "M:";
        String keyCorrectAnswer = "|";
        String questionMedia = "q";
        String answerMedia = "a";

        try (XWPFDocument document = new XWPFDocument(multipartFile.getInputStream())) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
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
                        if (text.toString().startsWith(keyQuestionStart)) {
                            String content = text.substring(keyMediaStart.length()).trim();
                            questionRequestDto.setContent(content);
                            typeMediaList = questionMedia;
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
                    }
                }

                questionRequestDtos.add(questionRequestDto);
            }
        } catch (IOException exception) {
            log.error("Error when import file: {}", exception.getMessage());
        }
        return questionRequestDtos;
    }

    private String getFileExtension(XWPFPictureData xwpfPictureData) {
        if (xwpfPictureData.suggestFileExtension() != null) return xwpfPictureData.suggestFileExtension();
        return xwpfPictureData.getFileName().substring(xwpfPictureData.getFileName().lastIndexOf('.') + 1);
    }
}
