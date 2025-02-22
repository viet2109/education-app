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
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WordDocQuizImportStrategy implements QuizImportStrategy {

    private static final Logger log = LoggerFactory.getLogger(WordQuizImportStrategy.class);

    private static final String KEY_QUIZ_START = "Quiz:";
    private static final String KEY_QUIZ_CATEGORY_START = "Category:";
    private static final String KEY_IMAGE_START = "Images:";
    private static final String KEY_QUESTION_START = "Question:";
    private static final String KEY_ANSWER_START = "Answer:";
    private static final String KEY_CORRECT_ANSWER = "|";
    private static final String IMAGE_SPLIT = "-";

    private static final String QUESTION_MEDIA = "q";
    private static final String ANSWER_MEDIA = "a";

    private final QuizService quizService;
    private final QuestionClient questionClient;

    @Override
    public QuizResponseDto convertFileToDto(MultipartFile multipartFile, String userId) {
        try (HWPFDocument document = new HWPFDocument(multipartFile.getInputStream())) {
            Range range = document.getRange();

            QuizRequestDto quizRequestDto = QuizRequestDto.builder().createdBy(userId).build();
            List<QuestionRequestFeignDto> questionRequestDtos = new ArrayList<>();

            List<Picture> allPictures = getAllPictures(document);
            List<MultipartFile> questionFiles = new ArrayList<>();
            List<MultipartFile> answerFiles = new ArrayList<>();
            List<String> questionFilesKey = new ArrayList<>();
            List<String> answerFilesKey = new ArrayList<>();

            int fileIndex = 0;

            for (int i = 0; i < range.numParagraphs(); i++) {
                Paragraph paragraph = range.getParagraph(i);
                String paragraphText = paragraph.text().trim();
                if (paragraphText.isEmpty()) continue;

                QuestionRequestFeignDto questionRequestDto = initializeQuestionDto();
                String typeMediaList = "";
                int indexAnswer = -1;

                for (String lineText : paragraphText.split("\u000b")) {
                    if (lineText.trim().startsWith(KEY_IMAGE_START)) {
                        fileIndex = processImageLine(lineText, typeMediaList, allPictures, questionFiles, answerFiles, questionFilesKey, answerFilesKey, questionRequestDto, indexAnswer, fileIndex);
                    } else {
                        processTextLine(lineText, quizRequestDto, questionRequestDto);
                        if (lineText.startsWith(KEY_ANSWER_START)) {
                            typeMediaList = ANSWER_MEDIA;
                            indexAnswer++;
                        } else if (lineText.startsWith(KEY_QUESTION_START)) {
                            typeMediaList = QUESTION_MEDIA;
                        }
                    }
                }

                if (questionRequestDto.getContent() != null && !questionRequestDto.getContent().isEmpty()) {
                    questionRequestDtos.add(questionRequestDto);
                }
            }

            return saveQuizAndQuestions(quizRequestDto, questionRequestDtos, questionFiles, questionFilesKey, answerFiles, answerFilesKey);
        } catch (IOException e) {
            log.error("Error when importing file: {}", e.getMessage());
        }
        return null;
    }

    private List<Picture> getAllPictures(HWPFDocument document) {
        return document.getPicturesTable().getAllPictures();
    }

    private int processImageLine(String lineText, String typeMediaList, List<Picture> allPictures,
                                 List<MultipartFile> questionFiles, List<MultipartFile> answerFiles,
                                 List<String> questionFilesKey, List<String> answerFilesKey,
                                 QuestionRequestFeignDto questionRequestDto, int indexAnswer, int fileIndex) {
        String[] imageParts = lineText.split(IMAGE_SPLIT, -1);
        for (int i = 0; i < imageParts.length; i++) {
            if (fileIndex >= allPictures.size()) break;
            MultipartFile file = createMultipartFile(allPictures.get(fileIndex));

            if (QUESTION_MEDIA.equals(typeMediaList)) {
                questionFiles.add(file);
                questionFilesKey.add(String.valueOf(fileIndex));
                questionRequestDto.getFilesIndex().add(String.valueOf(fileIndex++));
            } else if (ANSWER_MEDIA.equals(typeMediaList) && indexAnswer >= 0) {
                answerFiles.add(file);
                answerFilesKey.add(String.valueOf(fileIndex));
                questionRequestDto.getListAnswer().get(indexAnswer).getFilesIndex().add(String.valueOf(fileIndex++));
            }
        }
        return fileIndex;
    }

    private MultipartFile createMultipartFile(Picture pictureData) {
        return new CustomMultipartFile(
                "image",
                "image",
                "image/" + getFileExtension(pictureData),
                pictureData.getContent()
        );
    }

    private String getFileExtension(Picture pictureData) {
        String extension = pictureData.suggestFileExtension();
        return (extension != null) ? extension : "png";
    }

    private void processTextLine(String lineText, QuizRequestDto quizRequestDto, QuestionRequestFeignDto questionRequestDto) {
        if (lineText.startsWith(KEY_QUIZ_START)) {
            quizRequestDto.setTitle(lineText.substring(KEY_QUIZ_START.length()).trim());
        } else if (lineText.startsWith(KEY_QUIZ_CATEGORY_START)) {
            try {
                Category category = Category.valueOf(lineText.substring(KEY_QUIZ_CATEGORY_START.length()).trim().toUpperCase());
                quizRequestDto.setCategory(category);
            } catch (IllegalArgumentException e) {
                log.error("Invalid category: {}", lineText);
            }
        } else if (lineText.startsWith(KEY_QUESTION_START)) {
            questionRequestDto.setContent(lineText.substring(KEY_QUESTION_START.length()).trim());
        } else if (lineText.startsWith(KEY_ANSWER_START)) {
            String content = lineText.substring(KEY_ANSWER_START.length(), lineText.lastIndexOf(KEY_CORRECT_ANSWER)).trim();
            boolean isCorrect = Boolean.parseBoolean(lineText.substring(lineText.lastIndexOf(KEY_CORRECT_ANSWER) + 1).trim());
            AnswerRequestFeignDto answer = AnswerRequestFeignDto.builder().content(content).isCorrect(isCorrect).filesIndex(new ArrayList<>()).build();
            questionRequestDto.getListAnswer().add(answer);
        }
    }

    private QuestionRequestFeignDto initializeQuestionDto() {
        return QuestionRequestFeignDto.builder()
                .filesIndex(new ArrayList<>())
                .listAnswer(new ArrayList<>())
                .build();
    }

    private QuizResponseDto saveQuizAndQuestions(QuizRequestDto quizRequestDto,
                                                 List<QuestionRequestFeignDto> questionRequestDtos,
                                                 List<MultipartFile> questionFiles, List<String> questionFilesKey,
                                                 List<MultipartFile> answerFiles, List<String> answerFilesKey) throws IOException {
        long examId = quizService.createExam(quizRequestDto).getId();
        questionRequestDtos.forEach(question -> question.setExamId(examId));

        ObjectMapper objectMapper = new ObjectMapper();
        String questionsJson = objectMapper.writeValueAsString(questionRequestDtos);
        questionClient.createListQuestionByFeign(
                questionsJson,
                questionFilesKey.isEmpty() ? null : questionFilesKey,
                questionFiles.isEmpty() ? null : questionFiles,
                answerFilesKey.isEmpty() ? null : answerFilesKey,
                answerFiles.isEmpty() ? null : answerFiles
        );

        return quizService.getExamById(examId);
    }
}
