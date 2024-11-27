package bg.sofia.uni.fmi.mjt.glovo.exception;

public class NoSuchDelivery extends RuntimeException {
    public NoSuchDelivery(String message) {
        super(message);
    }

    public NoSuchDelivery(String message, Throwable cause) {
        super(message, cause);
    }
}
