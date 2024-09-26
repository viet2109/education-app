package com.studyapp.questionservice.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;

@Getter
public class RestApiClientException extends RuntimeException {
    private final String requestUrl;
    private final String responseBodyString; // Lưu chuỗi đã đọc từ Response.Body
    private final HttpStatus status;

    public RestApiClientException(String requestUrl, HttpStatus responseStatus, Response.Body responseBody) {
        super("Server error occurred while calling: " + requestUrl);
        this.requestUrl = requestUrl;
        this.status = responseStatus;
        this.responseBodyString = responseBody != null ? readResponseBody(responseBody) : ""; // Đọc ngay khi khởi tạo
    }

    public Object parseResponseBody() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Nếu responseBodyString không rỗng
        if (!responseBodyString.isEmpty()) {
            // Thử chuyển chuỗi thành JSON (HashMap)
            try {
                return objectMapper.readValue(responseBodyString, HashMap.class);
            } catch (IOException e) {
                // Nếu không phải JSON, trả về chính chuỗi
                return responseBodyString;
            }
        }

        // Trả về null nếu responseBodyString rỗng
        return null;
    }

    // Đọc nội dung từ Response.Body và chuyển thành String
    private String readResponseBody(Response.Body body) {
        try (var inputStream = body.asInputStream()) {
            return new String(inputStream.readAllBytes()); // Đọc tất cả bytes và chuyển thành String
        } catch (IOException e) {
            return ""; // Trả về chuỗi rỗng nếu có lỗi
        }
    }
}
