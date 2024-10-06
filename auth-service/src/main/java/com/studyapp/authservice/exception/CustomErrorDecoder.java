package com.studyapp.authservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;


public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        Response.Body responseBody = response.body();
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        if (responseStatus.is4xxClientError() || responseStatus.is5xxServerError()) {
            return new RestApiClientException(requestUrl, responseStatus, responseBody);
        }
        return errorDecoder.decode(methodKey, response);

    }
}
