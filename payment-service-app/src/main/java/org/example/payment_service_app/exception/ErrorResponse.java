package org.example.payment_service_app.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ErrorResponse {
    // Геттеры и сеттеры
    private String errorCode;
    private String message;
    private Object details;
    private LocalDateTime timestamp;

    // Конструкторы

    public ErrorResponse(String errorCode, String message, LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
    }
}
