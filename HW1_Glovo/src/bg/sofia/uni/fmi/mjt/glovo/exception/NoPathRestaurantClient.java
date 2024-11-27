package bg.sofia.uni.fmi.mjt.glovo.exception;

public class NoPathRestaurantClient extends RuntimeException {
    public NoPathRestaurantClient(String message) {
        super(message);
    }

    public NoPathRestaurantClient(String message, Throwable cause) {
        super(message, cause);
    }
}
