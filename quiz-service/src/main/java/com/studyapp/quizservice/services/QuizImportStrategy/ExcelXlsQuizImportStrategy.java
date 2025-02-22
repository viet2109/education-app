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
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExcelXlsQuizImportStrategy implements QuizImportStrategy {
    private static final Logger log = LoggerFactory.getLogger(ExcelQuizImportStrategy.class);
    private static final String QUIZ_SHEET_NAME = "Quiz";
    private static final String QUESTION_SHEET_NAME = "Questions";
    private static final String IMAGE_SHEET_NAME = "Images";
    private final QuizService quizService;
    private final QuestionClient questionClient;

    @Override
    public QuizResponseDto convertFileToDto(MultipartFile multipartFile, String userId) {
        List<QuestionRequestFeignDto> questions = new ArrayList<>();
        List<MultipartFile> questionFiles = new ArrayList<>();
        List<String> questionFilesKey = new ArrayList<>();
        List<String> answerFilesKey = new ArrayList<>();
        List<MultipartFile> answerFiles = new ArrayList<>();
        Long examId;

        try (Workbook workbook = new HSSFWorkbook(multipartFile.getInputStream())) {
            Sheet quizSheet = workbook.getSheet(QUIZ_SHEET_NAME);
            Sheet questionSheet = workbook.getSheet(QUESTION_SHEET_NAME);

            if (quizSheet == null) {
                log.error("Sheet 'Quiz' not found in workbook.");
                return null;
            }

            int quizRowIndex = 1; // Skip header row
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            Row rowQuiz = quizSheet.getRow(quizRowIndex);

            if (rowQuiz != null && !isRowEmpty(rowQuiz)) {
                QuizRequestDto quiz = new QuizRequestDto();
                quiz.setCreatedBy(userId);

                String title = getCellValue(rowQuiz, 0).trim();
                String category = getCellValue(rowQuiz, 1).trim();
                String durationStr = getCellValue(rowQuiz, 2).trim();
                String expiratedAtStr = getCellValue(rowQuiz, 3).trim();

                if (!title.isEmpty()) {
                    quiz.setTitle(title);
                }

                if (!category.isEmpty()) {
                    try {
                        quiz.setCategory(Category.valueOf(category.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid category: " + category);
                    }
                }

                if (!durationStr.isEmpty()) {
                    try {
                        quiz.setDuration(Integer.parseInt(durationStr.split("\\.")[0]));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid duration: " + durationStr);
                    }
                }

                if (!expiratedAtStr.isEmpty()) {
                    try {
                        quiz.setExpiratedAt(LocalDateTime.parse(expiratedAtStr, dateTimeFormatter));
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid expiratedAt format: " + expiratedAtStr);
                    }
                }

                examId = quizService.createExam(quiz).getId();

            } else {
                throw new IllegalArgumentException("No valid data found to create the quiz.");
            }

            if (questionSheet == null) {
                log.error("Sheet 'Questions' not found in workbook.");
                return null;
            }

            int rowIndex = 1; // Skip header row
            while (rowIndex < questionSheet.getPhysicalNumberOfRows()) {
                Row row = questionSheet.getRow(rowIndex);
                if (row != null && !getCellValue(row, 0).isEmpty()) {
                    QuestionRequestFeignDto question = processQuestion(workbook, examId, questionFilesKey, questionFiles, row);
                    questions.add(question);
                    rowIndex = processAnswers(workbook, answerFilesKey, answerFiles, questionSheet, rowIndex + 1, question);
                } else {
                    rowIndex++;
                }
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String questionRequestFeignDtoListJson = objectMapper.writeValueAsString(questions);

            questionClient.createListQuestionByFeign(
                    questionRequestFeignDtoListJson,
                    questionFilesKey.isEmpty() ? null : questionFilesKey,
                    questionFiles.isEmpty() ? null : questionFiles,
                    answerFilesKey.isEmpty() ? null : answerFilesKey,
                    answerFiles.isEmpty() ? null : answerFiles
            );

            return quizService.getExamById(examId);

        } catch (IOException e) {
            log.error("Error while importing file: {}", e.getMessage());
            return null;
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private QuestionRequestFeignDto processQuestion(Workbook workbook, Long examId, List<String> questionFilesKey, List<MultipartFile> questionFiles, Row row) {
        QuestionRequestFeignDto question = new QuestionRequestFeignDto();
        if (examId != null) {
            question.setExamId(examId);
        }
        question.setContent(getCellValue(row, 0));
        question.setFilesIndex(new ArrayList<>());

        String imageLinks = getCellValue(row, 3);
        if (!imageLinks.isEmpty()) {
            List<String> cellAddresses = parseCellAddresses(imageLinks);
            question.getFilesIndex().addAll(cellAddresses);

            cellAddresses.forEach(ca -> {
                MultipartFile file = getImageFromCell(workbook, IMAGE_SHEET_NAME, ca);
                if (file != null) {
                    questionFiles.add(file);
                    questionFilesKey.add(ca);
                }
            });
        }

        return question;
    }

    private int processAnswers(
            Workbook workbook,
            List<String> answerFilesKey,
            List<MultipartFile> answerFiles,
            Sheet questionSheet,
            int rowIndex,
            QuestionRequestFeignDto question
    ) {
        List<AnswerRequestFeignDto> answers = new ArrayList<>();

        while (rowIndex < questionSheet.getPhysicalNumberOfRows()) {
            Row answerRow = questionSheet.getRow(rowIndex);

            if (answerRow != null && getCellValue(answerRow, 0).isEmpty()) {
                AnswerRequestFeignDto answer = new AnswerRequestFeignDto();
                answer.setContent(getCellValue(answerRow, 1));
                answer.setIsCorrect(getBooleanCellValue(answerRow));
                answer.setFilesIndex(new ArrayList<>());

                String imageLinks = getCellValue(answerRow, 3);
                if (!imageLinks.isEmpty()) {
                    List<String> cellAddresses = parseCellAddresses(imageLinks);
                    answer.getFilesIndex().addAll(cellAddresses);

                    cellAddresses.forEach(ca -> {
                        MultipartFile file = getImageFromCell(workbook, IMAGE_SHEET_NAME, ca);
                        if (file != null) {
                            answerFiles.add(file);
                            answerFilesKey.add(ca);
                        }
                    });
                }

                answers.add(answer);
            } else {
                break;
            }

            rowIndex++;
        }

        question.setListAnswer(answers.isEmpty() ? new ArrayList<>() : answers);
        return rowIndex;
    }

    private MultipartFile getImageFromCell(Workbook workbook, String sheetName, String cellAddress) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (!(sheet instanceof HSSFSheet hssfSheet)) {
            log.error("Sheet '{}' does not support images.", sheetName);
            return null;
        }

        HSSFPatriarch drawing = hssfSheet.getDrawingPatriarch();
        if (drawing == null) {
            log.warn("No drawings found in sheet '{}'.", sheetName);
            return null;
        }

        CellAddress address = new CellAddress(cellAddress);
        for (HSSFShape shape : drawing.getChildren()) {
            if (shape instanceof HSSFPicture picture) {
                HSSFClientAnchor anchor = (HSSFClientAnchor) picture.getAnchor();
                if (anchor.getRow1() == address.getRow() && anchor.getCol1() == address.getColumn()) {
                    HSSFPictureData pictureData = picture.getPictureData();
                    String fileName = "image" + pictureData.hashCode() + "." + pictureData.suggestFileExtension();

                    return new CustomMultipartFile(
                            fileName,
                            fileName,
                            pictureData.getMimeType(),
                            pictureData.getData()
                    );
                }
            }
        }

        log.warn("No image found at cell '{}'.", cellAddress);
        return null;
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        // Trả về số nguyên
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                yield String.valueOf((int) cell.getNumericCellValue()); // Xử lý nếu là ngày
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private boolean getBooleanCellValue(Row row) {
        return Optional.ofNullable(row)
                .map(r -> r.getCell(2))
                .map(cell -> {
                    try {
                        return cell.getBooleanCellValue();
                    } catch (IllegalStateException e) {
                        log.warn("Invalid boolean value at row {}, column {}", row.getRowNum(), 2);
                        return false;
                    }
                })
                .orElse(false);
    }

    private List<String> parseCellAddresses(String imageLinks) {
        return Arrays.stream(imageLinks.split(";"))
                .map(cell -> cell.replace("Images!", "").trim())
                .toList();
    }
}
