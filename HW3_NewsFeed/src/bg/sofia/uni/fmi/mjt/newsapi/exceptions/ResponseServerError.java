package bg.sofia.uni.fmi.mjt.newsapi.exceptions;

public class ResponseServerError extends NewsClientException {
    public ResponseServerError(String message) {
        super(message);
    }

    public ResponseServerError(String message, Throwable cause) {
        super(message, cause);
    }
}
