package com.studyapp.quizservice.services.QuizImportStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyapp.quizservice.client.question.QuestionClient;
import com.studyapp.quizservice.client.question.dto.request.AnswerRequestFeignDto;
import com.studyapp.quizservice.client.question.dto.request.QuestionRequestFeignDto;
import com.studyapp.quizservice.dto.request.QuizRequestDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import com.studyapp.quizservice.enums.Category;
import com.studyapp.quizservice.services.QuizService;
import com.studyapp.quizservice.utils.CustomMultipartFile;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WordQuizImportStrategy implements QuizImportStrategy {
    private static final Logger log = LoggerFactory.getLogger(WordQuizImportStrategy.class);
    private final QuizService quizService;
    private final QuestionClient questionClient;

    @Override
    public QuizResponseDto convertFileToDto(MultipartFile multipartFile, String userId) {
        String keyQuizStart = "Quiz:";
        String keyQuizCategoryStart = "Category:";
        String keyImageStart = "Images:";
        String keyQuestionStart = "Question:";
        String keyAnswerStart = "Answer:";
        String keyCorrectAnswer = "|";
        String questionMedia = "q";
        String answerMedia = "a";
        String imageSplit = "-";

        try (XWPFDocument document = new XWPFDocument(multipartFile.getInputStream())) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            QuizRequestDto quizRequestDto = QuizRequestDto.builder().createdBy(userId).build();
            List<QuestionRequestFeignDto> questionRequestDtos = new ArrayList<>();
            List<XWPFPictureData> allPictures = getAllPictures(document);
            List<MultipartFile> questionFiles = new ArrayList<>();
            List<String> questionFilesKey = new ArrayList<>();
            List<String> answerFilesKey = new ArrayList<>();
            List<MultipartFile> answerFiles = new ArrayList<>();
            int fileIndex = 0;

            for (XWPFParagraph paragraph : paragraphs) {
                String paragraphText = paragraph.getText().trim();
                if (paragraphText.isEmpty()) continue;
                String typeMediaList = "";
                int indexAnswer = -1;
                QuestionRequestFeignDto questionRequestDto = QuestionRequestFeignDto
                        .builder()
                        .filesIndex(new ArrayList<>())
                        .listAnswer(new ArrayList<>())
                        .build();

                for (String lineText : paragraphText.split("\n")) {
                    //Image
                    if (lineText.trim().startsWith(keyImageStart)) {
                        for (int i = 0; i < lineText.trim().split(imageSplit, -1).length; i++) {
                            MultipartFile file = new CustomMultipartFile(
                                    allPictures.get(fileIndex).getFileName(),
                                    allPictures.get(fileIndex).getFileName(),
                                    "image/" + getFileExtension(allPictures.get(fileIndex)),
                                    allPictures.get(fileIndex).getData()
                            );
                            if (typeMediaList.equals(questionMedia)) {
                                if (fileIndex < allPictures.size()) {
                                    questionFiles.add(file);
                                    questionFilesKey.add(String.valueOf(fileIndex));
                                    questionRequestDto.getFilesIndex().add(String.valueOf(fileIndex++));
                                }
                            } else if (typeMediaList.equals(answerMedia)) {
                                if (fileIndex < allPictures.size()) {
                                    answerFiles.add(file);
                                    answerFilesKey.add(String.valueOf(fileIndex));
                                    questionRequestDto.getListAnswer().get(indexAnswer).getFilesIndex().add(String.valueOf(fileIndex++));
                                }
                            }
                        }
                    }
                    // text line
                    else {
                        if (lineText.startsWith(keyQuizStart)) {
                            String title = lineText.substring(keyQuizStart.length()).trim();
                            quizRequestDto.setTitle(title);
                        } else if (lineText.startsWith(keyQuizCategoryStart)) {
                            String categoryName = lineText.substring(keyQuizCategoryStart.length()).trim();
                            try {
                                Category category = Category.valueOf(categoryName.toUpperCase());
                                quizRequestDto.setCategory(category);

                            } catch (IllegalArgumentException exception) {
                                log.error("Category type has no constant with the specified name: {}", categoryName);
                            }
                        } else if (lineText.startsWith(keyQuestionStart)) {
                            String content = lineText.substring(keyQuestionStart.length()).trim();
                            questionRequestDto.setContent(content);
                            typeMediaList = questionMedia;
                        } else if (lineText.startsWith(keyAnswerStart)) {
                            String content = lineText.substring(keyAnswerStart.length(), lineText.lastIndexOf(keyCorrectAnswer)).trim();
                            boolean isCorrect = Boolean.parseBoolean(lineText.substring(lineText.lastIndexOf(keyCorrectAnswer) + 1).trim());
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
                    }
                }
                if (questionRequestDto.getContent() != null && !questionRequestDto.getContent().trim().isEmpty())
                    questionRequestDtos.add(questionRequestDto);
            }

            long examId = quizService.createExam(quizRequestDto).getId();
            questionRequestDtos.forEach(questionRequestFeignDto -> questionRequestFeignDto.setExamId(examId));

            ObjectMapper objectMapper = new ObjectMapper();
            String questionRequestFeignDtoListJson = objectMapper.writeValueAsString(questionRequestDtos);
            questionClient.createListQuestionByFeign(questionRequestFeignDtoListJson, questionFilesKey.isEmpty() ? null : questionFilesKey, questionFiles.isEmpty() ? null : questionFiles, answerFilesKey.isEmpty() ? null : answerFilesKey, answerFiles.isEmpty() ? null : answerFiles);

            return quizService.getExamById(examId);

        } catch (IOException exception) {
            log.error("Error when import file: {}", exception.getMessage());
        }
        return null;
    }


    private List<XWPFPictureData> getAllPictures(XWPFDocument document) {
        return document.getParagraphs().stream()
                .flatMap(paragraph -> paragraph.getRuns().stream())
                .flatMap(run -> run.getEmbeddedPictures().stream())
                .map(XWPFPicture::getPictureData)
                .toList();
    }


    private String getFileExtension(XWPFPictureData xwpfPictureData) {
        if (xwpfPictureData.suggestFileExtension() != null) return xwpfPictureData.suggestFileExtension();
        return xwpfPictureData.getFileName().substring(xwpfPictureData.getFileName().lastIndexOf('.') + 1);
    }

}

