package org.example.payment_service_app.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
@Setter
public class ServiceException extends RuntimeException {

    private HttpStatus status;
    private String message;

    private static final String EMPTY_STRING = "";

    public ServiceException(ErrorMessage error) {
        this(error.getStatus(), error.getMessage());
        this.status = error.getStatus();
        this.message = getMessage(EMPTY_STRING);
    }

    public ServiceException(ErrorMessage error, int value) {
        this(error.getStatus(), error.getMessage());
        this.message = getMessage(Integer.toString(value));
    }

    public ServiceException(ErrorMessage error, long value) {
        this(error.getStatus(), error.getMessage());
        this.message = getMessage(Long.toString(value));
    }

    public ServiceException(ErrorMessage error, UUID value) {
        this(error.getStatus(), error.getMessage());
        this.message = getMessage(value.toString());
    }

    public ServiceException(ErrorMessage error, String value) {
        this(error.getStatus(), error.getMessage());
        this.message = getMessage(value);
    }

    public ServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    private String getMessage(String value) {
        if (this.message == null || !this.message.contains("%")) {
            return this.message != null ? this.message : "";
        }
        try {
            return String.format(this.message, value);
        } catch (Exception e) {
            return this.message.replace("%", "%%"); // Экранируем проблемный %
        }
    }
}
