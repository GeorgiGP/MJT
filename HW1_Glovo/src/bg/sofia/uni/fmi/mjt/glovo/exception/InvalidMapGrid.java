package bg.sofia.uni.fmi.mjt.glovo.exception;

public class InvalidMapGrid extends RuntimeException {
    public InvalidMapGrid(String message) {
        super(message);
    }

    public InvalidMapGrid(String message, Throwable cause) {
        super(message, cause);
    }
}
