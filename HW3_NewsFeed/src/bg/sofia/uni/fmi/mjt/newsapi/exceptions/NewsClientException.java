package bg.sofia.uni.fmi.mjt.newsapi.exceptions;

public class NewsClientException extends RuntimeException {

    public NewsClientException(String message) {
        super(message);
    }

    public NewsClientException(String message, Throwable e) {
        super(message, e);
    }

}
