package spring.practice.elmenus_lite.exception;

public class JwtGenerationFailedException extends RuntimeException {
    public JwtGenerationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

