package com.studyapp.quizservice.services.QuizExportStrategy;

import com.studyapp.quizservice.client.file.dto.Media;
import com.studyapp.quizservice.client.question.dto.response.AnswerChangeResponseDto;
import com.studyapp.quizservice.client.question.dto.response.QuestionChangeResponseDto;
import com.studyapp.quizservice.dto.response.QuizChangeResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

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
    public byte[] exportQuiz(QuizChangeResponseDto quizResponseDto) {
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream out = null;
        String keyQuizStart = "Quiz:";
        String keyQuizCategoryStart = "Category:";
        String keyQuestionStart = "Question:";
        String keyAnswerStart = "Answer:";
        String keyImageStart = "Images:";
        String imageSplit = "-";
        String keyCorrectAnswer = "|";

        try {
            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setText(String.format("%s %s", keyQuizStart, quizResponseDto.getTitle()));
            titleRun.addBreak(BreakType.TEXT_WRAPPING);
            titleRun.setText(String.format("%s %s", keyQuizCategoryStart, quizResponseDto.getCategory()));

            for (QuestionChangeResponseDto questionResponseDto : quizResponseDto.getListQuestion()) {
                String questionContent = questionResponseDto.getContent();
                XWPFParagraph questionParagraph = document.createParagraph();
                XWPFRun questionParagraphRun = questionParagraph.createRun();
                questionParagraphRun.setText(String.format("%s %s", keyQuestionStart, questionContent.trim()));
                questionParagraphRun.addBreak(BreakType.TEXT_WRAPPING);

                if (!questionResponseDto.getFiles().isEmpty()) {
                    questionParagraphRun.setText(String.format("%s ", keyImageStart));
                    for (int i = 0; i < questionResponseDto.getFiles().size(); i++) {
                        Media media = questionResponseDto.getFiles().get(i);
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
                            if (i < questionResponseDto.getFiles().size() - 1) {
                                questionParagraphRun.setText(imageSplit);
                            }
                        } catch (InvalidFormatException e) {
                            log.error("Error when format type picture: {}", e.getMessage());
                        }
                        tempFile.delete();
                    }
                    questionParagraphRun.addBreak(BreakType.TEXT_WRAPPING);
                }

                for (AnswerChangeResponseDto answerResponseDto : questionResponseDto.getListAnswer()) {
                    String answerContent = answerResponseDto.getContent();
                    questionParagraphRun.setText(String.format("%s %s %s %s", keyAnswerStart, answerContent.trim(), keyCorrectAnswer, answerResponseDto.getIsCorrect()));
                    questionParagraphRun.addBreak(BreakType.TEXT_WRAPPING);

                    if (!answerResponseDto.getFiles().isEmpty()) {
                        questionParagraphRun.setText(String.format("%s ", keyImageStart));

                        for (int i = 0; i < answerResponseDto.getFiles().size(); i++) {
                            Media media = answerResponseDto.getFiles().get(i);
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
                                if (i < answerResponseDto.getFiles().size() - 1) {
                                    questionParagraphRun.setText(imageSplit);
                                }
                            } catch (InvalidFormatException e) {
                                log.error("Error when format type picture: {}", e.getMessage());
                            }
                            tempFile.delete();
                        }
                        questionParagraphRun.addBreak(BreakType.TEXT_WRAPPING);
                    }
                }
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