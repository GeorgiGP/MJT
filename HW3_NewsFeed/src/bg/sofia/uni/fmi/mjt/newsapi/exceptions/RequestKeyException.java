package bg.sofia.uni.fmi.mjt.newsapi.exceptions;

public class RequestKeyException extends NewsClientException {
    public RequestKeyException(String message) {
        super(message);
    }

    public RequestKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
