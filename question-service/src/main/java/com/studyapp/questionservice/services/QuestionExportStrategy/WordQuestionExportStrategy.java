package com.studyapp.questionservice.services.QuestionExportStrategy;

import com.studyapp.questionservice.dto.response.AnswerResponseDto;
import com.studyapp.questionservice.dto.response.QuestionResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class WordQuestionExportStrategy implements QuestionExportStrategy {
    @Override
    public byte[] exportQuestions(List<QuestionResponseDto> questionResponseDtos) {
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream out = null;
        try {
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("List of question");
            titleRun.setFontSize(18);
            titleRun.setBold(true);

            int questionIndex = 1;
            for (QuestionResponseDto questionResponseDto : questionResponseDtos) {
                String questionContent = questionResponseDto.getContent();
                XWPFParagraph questionParagraph = document.createParagraph();
                XWPFRun questionParagraphRun = questionParagraph.createRun();
                questionParagraphRun.setText(String.format("Question %s: %s", questionIndex, questionContent.trim()));
                questionParagraphRun.addBreak(BreakType.TEXT_WRAPPING);
                char answerIndex = 'A';
                if (!questionResponseDto.getFilesUrl().isEmpty()) {
                    for (String fileUrl : questionResponseDto.getFilesUrl()) {
                        URL url = URI.create(fileUrl).toURL();
                        BufferedImage image = ImageIO.read(url);
                        if (image == null) {
                            log.error("This fileUrl is not image type");
                            return null;
                        }
                        String imageFormat = getImageFormat(fileUrl);
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
                    if (!answerResponseDto.getFilesUrl().isEmpty()) {
                        for (String fileUrl : answerResponseDto.getFilesUrl()) {
                            URL url = URI.create(fileUrl).toURL();
                            BufferedImage image = ImageIO.read(url);
                            if (image == null) {
                                log.error("This fileUrl is not image type");
                                return null;
                            }
                            String imageFormat = getImageFormat(fileUrl);
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

    private String getImageFormat(String imageUrl) {
        // Sử dụng regex để kiểm tra định dạng hình ảnh từ URL
        log.info(imageUrl);
        String regex = ".*\\.(jpg|jpeg|png|gif|bmp)(\\?.*)?$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(imageUrl);

        if (matcher.find()) {
            return matcher.group(1); // Trả về định dạng hình ảnh
        }

        log.info(matcher.group(1));

        // Nếu không tìm thấy, bạn có thể kiểm tra Content-Type từ HTTP response nếu cần
        throw new IllegalArgumentException("Unsupported image format");
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
