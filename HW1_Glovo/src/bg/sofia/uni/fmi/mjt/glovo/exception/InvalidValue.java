package bg.sofia.uni.fmi.mjt.glovo.exception;

public class InvalidValue extends RuntimeException {
    public InvalidValue(String message) {
        super(message);
    }

    public InvalidValue(String message, Throwable cause) {
        super(message, cause);
    }
}
