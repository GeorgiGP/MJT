package bg.sofia.uni.fmi.mjt.newsapi.exceptions;

public class RequestArgumentsException extends NewsClientException {
    public RequestArgumentsException(String message) {
        super(message);
    }

    public RequestArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
