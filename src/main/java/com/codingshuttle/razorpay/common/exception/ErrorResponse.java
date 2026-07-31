package com.codingshuttle.razorpay.common.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        String errorCode,
        String errorDescription,
        LocalDateTime timestamp,
        List<String> fieldErrors
) {
    public record FieldError(
            String field,
            String message
    ) {}

    public static ErrorResponse of(String errorCode, String errorDescription) {
        return new ErrorResponse(errorCode, errorDescription, LocalDateTime.now(), null);
    }

    public static ErrorResponse of(String errorCode, String errorDescription, LocalDateTime timestamp, List<String> fieldErrors) {
        return new ErrorResponse(errorCode, errorDescription, timestamp, fieldErrors);
    }
}
