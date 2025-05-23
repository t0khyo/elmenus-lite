package spring.practice.elmenus_lite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        int value = ex.getStatusCode().value();
        ErrorResponse errorResponse = new ErrorResponse(
                value,
                HttpStatus.valueOf(value).getReasonPhrase(),
                ex.getReason(),
                System.currentTimeMillis() // You can use a custom timestamp
        );
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }
}
