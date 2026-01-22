package org.example.payment_service_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ExceptionMessageModel> handleException(ServiceException ex) {
        final ExceptionMessageModel errorMessage = new ExceptionMessageModel(ex.getMessage());
        logger.error(ex.getMessage());
        return new ResponseEntity<>(errorMessage, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessageModel> handleException(Exception ex) {
        final ExceptionMessageModel errorMessage = new ExceptionMessageModel(ex.getMessage());
        logger.error(ex.getMessage());
        logger.error(ex.getStackTrace());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
