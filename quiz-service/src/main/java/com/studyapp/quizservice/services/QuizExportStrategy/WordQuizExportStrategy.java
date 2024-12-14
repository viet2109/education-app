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
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            addQuizInfo(document, quizResponseDto);

            for (QuestionChangeResponseDto question : quizResponseDto.getListQuestion()) {
                addQuestionToDocument(document, question);
            }

            document.write(out);
            log.info("Word document created successfully!");
            return out.toByteArray();

        } catch (IOException | InvalidFormatException e) {
            log.error("Error while exporting quiz: {}", e.getMessage());
            return new byte[0];
        }
    }

    private void addQuizInfo(XWPFDocument document, QuizChangeResponseDto quizResponseDto) {
        XWPFParagraph titleParagraph = document.createParagraph();
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(String.format("Quiz: %s", quizResponseDto.getTitle()));
        titleRun.addBreak(BreakType.TEXT_WRAPPING);
        titleRun.setText(String.format("Category: %s", quizResponseDto.getCategory()));
        titleRun.addBreak(BreakType.TEXT_WRAPPING);
    }

    private void addQuestionToDocument(XWPFDocument document, QuestionChangeResponseDto question) throws IOException, InvalidFormatException {
        XWPFParagraph questionParagraph = document.createParagraph();
        XWPFRun questionRun = questionParagraph.createRun();
        questionRun.setText(String.format("Question: %s", question.getContent().trim()));
        questionRun.addBreak(BreakType.TEXT_WRAPPING);

        if (!question.getFiles().isEmpty()) {
            addMediaFiles(questionRun, question.getFiles(), "Images:");
        }

        for (AnswerChangeResponseDto answer : question.getListAnswer()) {
            addAnswerToDocument(questionRun, answer);
        }
    }

    private void addAnswerToDocument(XWPFRun run, AnswerChangeResponseDto answer) throws IOException, InvalidFormatException {
        run.setText(String.format("Answer: %s | Correct: %s", answer.getContent().trim(), answer.getIsCorrect()));
        run.addBreak(BreakType.TEXT_WRAPPING);

        if (!answer.getFiles().isEmpty()) {
            addMediaFiles(run, answer.getFiles(), "Images:");
        }
    }

    private void addMediaFiles(XWPFRun run, java.util.List<Media> files, String label) throws IOException, InvalidFormatException {
        run.setText(label);

        for (int i = 0; i < files.size(); i++) {
            Media media = files.get(i);
            BufferedImage image = fetchImageFromUrl(media.getFileUrl());

            if (image != null) {
                String imageFormat = getImageFormat(media.getFileType());
                try (InputStream imageData = convertImageToInputStream(image, imageFormat)) {
                    int pictureType = getPictureType(imageFormat);
                    run.addPicture(imageData, pictureType, "image", Units.toEMU(100), Units.toEMU(50));
                    if (i < files.size() - 1) {
                        run.setText(" - ");
                    }
                }
            }
        }
        run.addBreak(BreakType.TEXT_WRAPPING);
    }

    private BufferedImage fetchImageFromUrl(String fileUrl) {
        try {
            URL url = URI.create(fileUrl).toURL();
            return ImageIO.read(url);
        } catch (IOException e) {
            log.error("Failed to fetch image from URL {}: {}", fileUrl, e.getMessage());
            return null;
        }
    }

    private InputStream convertImageToInputStream(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, format, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    private String getImageFormat(String fileType) {
        String regex = "/([^/]+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileType);

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
