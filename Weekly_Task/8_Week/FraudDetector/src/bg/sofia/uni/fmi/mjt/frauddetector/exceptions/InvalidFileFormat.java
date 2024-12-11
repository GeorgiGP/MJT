package bg.sofia.uni.fmi.mjt.frauddetector.exceptions;

public class InvalidFileFormat extends RuntimeException {
    public InvalidFileFormat(String message) {
        super(message);
    }

    public InvalidFileFormat(String message, Throwable cause) {
        super(message, cause);
    }
}
