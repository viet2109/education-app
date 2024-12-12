package com.studyapp.quizservice.services.QuizExportStrategy;

import com.studyapp.quizservice.client.file.dto.Media;
import com.studyapp.quizservice.client.question.dto.response.AnswerChangeResponseDto;
import com.studyapp.quizservice.client.question.dto.response.QuestionChangeResponseDto;
import com.studyapp.quizservice.dto.response.QuizChangeResponseDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class ExcelQuizExportStrategy implements QuizExportStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ExcelQuizExportStrategy.class);

    @Override
    public byte[] exportQuiz(QuizChangeResponseDto quizResponseDto) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Create sheets
            Sheet quizSheet = workbook.createSheet("Quiz");
            Sheet questionSheet = workbook.createSheet("Questions");
            Sheet imageSheet = workbook.createSheet("Images");
            setupSheetStyles(quizSheet, questionSheet, imageSheet);

            // Populate quiz and questions
            populateQuizSheet(quizSheet, quizResponseDto);
            populateQuestionSheet(workbook, questionSheet, imageSheet, quizResponseDto);

            // Write workbook to output stream
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            logger.error("Error exporting quiz to Excel: ", e);
            throw new RuntimeException("Failed to export quiz", e);
        }
    }

    private void setupSheetStyles(Sheet quizSheet, Sheet questionSheet, Sheet imageSheet) {
        quizSheet.setColumnWidth(0, quizSheet.getColumnWidth(0) * 6);
        quizSheet.setColumnWidth(1, quizSheet.getColumnWidth(1) * 3);
        quizSheet.setColumnWidth(3, quizSheet.getColumnWidth(3) * 2);
        questionSheet.setColumnWidth(0, questionSheet.getColumnWidth(0) * 6);
        questionSheet.setColumnWidth(1, questionSheet.getColumnWidth(1) * 6);
        questionSheet.setColumnWidth(3, questionSheet.getColumnWidth(3) * 3);
        imageSheet.setColumnWidth(0, imageSheet.getColumnWidth(0) * 2);
    }

    private CellStyle getWrapTextStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        return style;
    }

    private void populateQuizSheet(Sheet quizSheet, QuizChangeResponseDto quiz) {
        Row headerRow = quizSheet.createRow(0);
        String[] headers = {"Title", "Category", "Duration", "Expiration Date"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        Row row = quizSheet.createRow(1);
        row.createCell(0).setCellValue(quiz.getTitle());
        row.getCell(0).setCellStyle(getWrapTextStyle(quizSheet.getWorkbook()));
        row.createCell(1).setCellValue(quiz.getCategory().toString());
        row.createCell(2).setCellValue(quiz.getDuration());
        row.createCell(3).setCellValue(quiz.getExpiratedAt());
    }

    private void populateQuestionSheet(Workbook workbook, Sheet questionSheet, Sheet imageSheet, QuizChangeResponseDto quiz) {
        int rowNum = 1;
        int imageRowNum = 0;

        Row headerRow = questionSheet.createRow(0);
        String[] headers = {"Question", "Answer", "Correct", "Image Links"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        for (QuestionChangeResponseDto question : quiz.getListQuestion()) {
            Row questionRow = questionSheet.createRow(rowNum++);
            questionRow.createCell(0).setCellValue(question.getContent());
            questionRow.getCell(0).setCellStyle(getWrapTextStyle(workbook));

            // Add question images
            String questionImageLinks = addImagesToSheet(workbook, imageSheet, question.getFiles(), imageRowNum);
            logger.info("questionImageLinks: {}", questionImageLinks);

            imageRowNum += question.getFiles() != null ? question.getFiles().size() : 0;
            questionRow.createCell(3).setCellValue(questionImageLinks);
            questionRow.getCell(0).setCellStyle(getWrapTextStyle(workbook));

            for (AnswerChangeResponseDto answer : question.getListAnswer()) {
                Row answerRow = questionSheet.createRow(rowNum++);
                answerRow.createCell(1).setCellValue(answer.getContent());
                answerRow.getCell(1).setCellStyle(getWrapTextStyle(workbook));
                answerRow.createCell(2).setCellValue(answer.getIsCorrect());

                // Add answer images
                String answerImageLinks = addImagesToSheet(workbook, imageSheet, answer.getFiles(), imageRowNum);
                imageRowNum += answer.getFiles() != null ? answer.getFiles().size() : 0;
                answerRow.createCell(3).setCellValue(answerImageLinks);
                answerRow.getCell(3).setCellStyle(getWrapTextStyle(workbook));
            }
        }

        // Set row height for images
        adjustImageRowHeights(imageSheet, imageRowNum);
    }

    private String addImagesToSheet(Workbook workbook, Sheet imageSheet, List<Media> images, int imageRowNum) {
        if (images == null || images.isEmpty()) return "";

        StringBuilder imageLinks = new StringBuilder();
        Drawing<?> drawing = imageSheet.createDrawingPatriarch();

        for (Media media : images) {
            try {
                BufferedImage image = ImageIO.read(new URL(URI.create(media.getFileUrl()).toURL().toString()));
                if (image == null) {
                    logger.warn("Invalid image URL: {}", media.getFileUrl());
                    continue;
                }

                String format = media.getFileType().split("/")[1].toLowerCase();
                int pictureIdx = addImageToWorkbook(workbook, image, format);

                ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
                anchor.setCol1(0);
                anchor.setRow1(imageRowNum);
                anchor.setCol2(1);
                anchor.setRow2(imageRowNum + 1);

                drawing.createPicture(anchor, pictureIdx);
                if (!imageLinks.isEmpty()) imageLinks.append("; ");
                imageLinks.append("Images!A").append(imageRowNum + 1);

                imageRowNum++;
            } catch (Exception e) {
                logger.error("Error processing image from URL: {}", media.getFileUrl(), e);
            }
        }
        return imageLinks.toString();
    }

    private int addImageToWorkbook(Workbook workbook, BufferedImage image, String format) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            int pictureType = switch (format) {
                case "png", "gif" -> Workbook.PICTURE_TYPE_PNG;
                case "jpeg", "jpg" -> Workbook.PICTURE_TYPE_JPEG;
                case "bmp" -> Workbook.PICTURE_TYPE_DIB;
                default -> throw new IllegalArgumentException("Unsupported image format: " + format);
            };
            return workbook.addPicture(imageBytes, pictureType);
        }
    }

    private void adjustImageRowHeights(Sheet imageSheet, int imageRowNum) {
        for (int i = 0; i < imageRowNum; i++) {
            Row row = imageSheet.getRow(i);
            if (row == null) row = imageSheet.createRow(i);
            row.setHeightInPoints(80); // Adjust height for images
        }
    }
}
