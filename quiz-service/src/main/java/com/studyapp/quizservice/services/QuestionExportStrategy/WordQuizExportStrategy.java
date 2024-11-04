package com.studyapp.quizservice.services.QuestionExportStrategy;

import com.studyapp.quizservice.client.file.dto.Media;
import com.studyapp.quizservice.client.question.dto.response.AnswerResponseDto;
import com.studyapp.quizservice.client.question.dto.response.QuestionResponseDto;
import com.studyapp.quizservice.dto.response.QuizResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class WordQuizExportStrategy implements QuizExportStrategy {
    
    @Override
    public byte[] exportQuiz(QuizResponseDto quizResponseDto) {
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream out = null;
        try {
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText(quizResponseDto.getTitle());
            titleRun.setFontSize(18);
            titleRun.setBold(true);

            int questionIndex = 1;
            for (QuestionResponseDto questionResponseDto : quizResponseDto.getListQuestion()) {
                String questionContent = questionResponseDto.getContent();
                XWPFParagraph questionParagraph = document.createParagraph();
                XWPFRun questionParagraphRun = questionParagraph.createRun();
                questionParagraphRun.setText(String.format("Question %s: %s", questionIndex, questionContent.trim()));
                questionParagraphRun.addBreak(BreakType.TEXT_WRAPPING);
                char answerIndex = 'A';
                if (!questionResponseDto.getFiles().isEmpty()) {
                    for (Media media : questionResponseDto.getFiles()) {
                        URL url = URI.create(media.getFileUrl()).toURL();
                        BufferedImage image = ImageIO.read(url);
                        if (image == null) {
                            log.error("This fileUrl is not image type");
                            return null;
                        }
                        String imageFormat = getImageFormat(media.getFileType());
                        File tempFile = File.createTempFile("tempImage", "." + imageFormat);
                        ImageIO.write(image, imageFormat, tempFile);
                        try (InputStream imageData = new FileInputStream(tempFile)) {
                            int pictureType = getPictureType(imageFormat);
                            questionParagraphRun.addPicture(imageData, pictureType, tempFile.getName(), Units.toEMU(100), Units.toEMU(50));
                            questionParagraphRun.addTab();
                        } catch (InvalidFormatException e) {
                            log.error("Error when format type picture: {}", e.getMessage());
                        }
                        tempFile.delete();
                    }
                    questionParagraphRun.addBreak(BreakType.TEXT_WRAPPING);
                }
                for (AnswerResponseDto answerResponseDto : questionResponseDto.getListAnswer()) {
                    String answerContent = answerResponseDto.getContent();
                    questionParagraphRun.setText(String.format("%s: %s", answerIndex, answerContent.trim()));
                    questionParagraphRun.addBreak(BreakType.TEXT_WRAPPING);
                    answerIndex++;
                    if (!answerResponseDto.getFiles().isEmpty()) {
                        for (Media media : answerResponseDto.getFiles()) {
                            URL url = URI.create(media.getFileUrl()).toURL();
                            BufferedImage image = ImageIO.read(url);
                            if (image == null) {
                                log.error("This fileUrl is not image type");
                                return null;
                            }
                            String imageFormat = getImageFormat(media.getFileType());
                            File tempFile = File.createTempFile("tempImage", "." + imageFormat);
                            ImageIO.write(image, imageFormat, tempFile);
                            try (InputStream imageData = new FileInputStream(tempFile)) {
                                int pictureType = getPictureType(imageFormat);
                                questionParagraphRun.addPicture(imageData, pictureType, tempFile.getName(), Units.toEMU(100), Units.toEMU(50));
                                questionParagraphRun.addTab();
                            } catch (InvalidFormatException e) {
                                log.error("Error when format type picture: {}", e.getMessage());
                            }
                            tempFile.delete();
                        }
                        questionParagraphRun.addBreak(BreakType.TEXT_WRAPPING);
                    }
                }
                questionIndex++;
            }

            out = new ByteArrayOutputStream();
            document.write(out);
            document.close();

            log.info("Tài liệu Word đã được tạo thành công!");
        } catch (IOException e) {
            log.error("Error occur when export: {}", e.getMessage());
        }
        return out != null ? out.toByteArray() : new byte[0];

    }

    private String getImageFormat(String imageType) {

        String regex = "/([^/]+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(imageType);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Unsupported image format");
        }
    }

    private int getPictureType(String imageFormat) {

        return switch (imageFormat.toLowerCase()) {
            case "png" -> XWPFDocument.PICTURE_TYPE_PNG;
            case "jpg", "jpeg" -> XWPFDocument.PICTURE_TYPE_JPEG;
            case "gif" -> XWPFDocument.PICTURE_TYPE_GIF;
            case "bmp" -> XWPFDocument.PICTURE_TYPE_BMP;
            default -> throw new IllegalArgumentException("Unsupported image format");
        };
    }

}
