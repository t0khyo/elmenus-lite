package spring.practice.elmenus_lite.exception;

import spring.practice.elmenus_lite.enums.ErrorMessage;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
