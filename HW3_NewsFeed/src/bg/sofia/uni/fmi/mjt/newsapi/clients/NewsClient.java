package bg.sofia.uni.fmi.mjt.newsapi.clients;

import bg.sofia.uni.fmi.mjt.newsapi.exceptions.RequestArgumentsException;
import bg.sofia.uni.fmi.mjt.newsapi.exceptions.RequestKeyException;
import bg.sofia.uni.fmi.mjt.newsapi.exceptions.ResponseServerError;
import bg.sofia.uni.fmi.mjt.newsapi.newsdata.NewsArticle;
import bg.sofia.uni.fmi.mjt.newsapi.exceptions.NewsClientException;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public abstract class NewsClient {
    protected record News(List<NewsArticle> articles) { }

    protected static final int MAX_NEWS_COUNT_TO_RECEIVE = 100;
    protected static final int MAX_PAGE_SIZE = 100;

    protected static final String API_ENDPOINT_SCHEME = "http";
    protected static final String API_ENDPOINT_HOST = "newsapi.org";
    protected static final String API_ENDPOINT_PATH = "/v2/top-headlines";
    protected static final String API_ENDPOINT_QUERY = "q=%s&country=%s&category=%s&apiKey=%s&pageSize=%d&page=%d";

    protected static final Gson GSON = new Gson();
    protected static final String CONCAT_SYMBOL = "+";

    protected final HttpClient newsHttpClient;
    protected final String apiKey;

    public NewsClient(HttpClient newsHttpClient, String apiKey) {
        if (newsHttpClient == null || apiKey == null) {
            throw new IllegalArgumentException("newsHttpClient and apiKey must not be null");
        }
        this.newsHttpClient = newsHttpClient;
        this.apiKey = apiKey;
    }

    protected int getNewsCount(String formatedKeywords, String country, String category) {
        record CountNews(int totalResults) { }

        HttpResponse<String> response = responseFromNewsApi(formatedKeywords, country, category, 0, 0);
        return GSON.fromJson(response.body(), CountNews.class).totalResults;

    }

    protected HttpResponse<String> responseFromNewsApi(String formatedKeywords, String country, String category,
                                                       int pageSize, int page) {
        HttpResponse<String> result;
        try {
            URI uri = new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH,
                    API_ENDPOINT_QUERY.formatted(formatedKeywords, country, category,
                            apiKey, pageSize, page), null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            result = newsHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new NewsClientException("Could not retrieve news with pageSize: " + pageSize + ",page: " +
                    page + ",category: " + category + ",country: " + country + ",keywords: " + formatedKeywords, e);
        }

        return switch (result.statusCode()) {
            case HttpURLConnection.HTTP_OK -> result;
            case HttpURLConnection.HTTP_UNAUTHORIZED -> throw new RequestKeyException("Invalid apiKey: " + apiKey);
            case HttpURLConnection.HTTP_BAD_REQUEST ->
                    throw new RequestArgumentsException("Invalid arguments: category: " + category +
                            ",country: " + country + ",keywords: " + formatedKeywords);
            case HttpURLConnection.HTTP_INTERNAL_ERROR -> throw new ResponseServerError("Server error");
            default -> throw new NewsClientException("Unexpected response code from news service: " +
                    result.statusCode());
        };
    }
}
