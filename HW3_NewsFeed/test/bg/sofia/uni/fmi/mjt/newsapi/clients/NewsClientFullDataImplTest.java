package bg.sofia.uni.fmi.mjt.newsapi.clients;

import bg.sofia.uni.fmi.mjt.newsapi.newsdata.NewsArticle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewsClientFullDataImplTest {
    private static final String newsBody =
            """
                    {
                      "status": "ok",
                      "totalResults": 2,
                      "articles": [
                        {
                          "author": "author",
                          "title": "title",
                          "description": "description",
                          "content": "content"
                        },
                        {
                          "author": "author2",
                          "title": "title2",
                          "description": "description2",
                          "content": "content2"
                        }]
                    }
                    """;

    @Mock
    private static HttpClient newsHttpClient = mock();

    @Mock
    private static HttpResponse<String> response = mock();

    private static NewsClientFullData newsClient;

    private static List<NewsArticle> news;

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        when(response.body()).thenReturn(newsBody);
        when(newsHttpClient.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(response);
        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        newsClient = new NewsClientFullDataImpl(newsHttpClient, "123");
        news = List.of(new NewsArticle("author", "title", "description", "content"),
                        new NewsArticle("author2", "title2", "description2", "content2"));
    }

    @Test
    void testGetAllNewsValidListWith2Articles() {
        assertIterableEquals(newsClient.getAllNews(List.of("fake"), "fake", "fake"), news,
                "Those news were not expected. Should return: " + news);
    }

    @Test
    void testGetNewsCountryValidListWith2Articles() {
        assertIterableEquals(newsClient.getNewsCountry(List.of("fake"), "fake"), news,
                "Those news were not expected. Should return: " + news);
    }

    @Test
    void testGetNewsCategoryValidListWith2Articles() {
        assertIterableEquals(newsClient.getNewsCategory(List.of("fake"), "fake"), news,
                "Those news were not expected. Should return: " + news);
    }

    @Test
    void testGetNewsWithOnlyKeywordsValidListWith2Articles() {
        assertIterableEquals(newsClient.getNews(List.of("fake")), news,
                "Those news were not expected. Should return: " + news);
    }

    @Test
    void testNewsClientFullDataImplConstructorNullHttpClient() {
        assertThrows(IllegalArgumentException.class,
                () -> new NewsClientFullDataImpl(null, "key"),
                "Keywords in parameter cannot be null");
    }

    @Test
    void testNewsClientFullDataImplConstructorNullKey() {
        assertThrows(IllegalArgumentException.class,
                () -> new NewsClientFullDataImpl(newsHttpClient, null),
                "Keywords in parameter cannot be null");
    }

    @Test
    void testGetAllNewsNullKeywords() {
        assertThrows(IllegalArgumentException.class,
                () -> newsClient.getAllNews(null, "a", "a"),
                "Keywords in parameter cannot be null");
    }

    @Test
    void testGetAllNewsNullCountry() {
        assertThrows(IllegalArgumentException.class,
                () -> newsClient.getAllNews(List.of("a"), null, "a"),
                "Country in parameter cannot be null");
    }

    @Test
    void testGetAllNewsNullCategory() {
        assertThrows(IllegalArgumentException.class,
                () -> newsClient.getAllNews(List.of("a"), "a", null),
                "Category in parameter cannot be null");
    }
    @Test
    void testGetAllNewsNullWordInKeywords() {
        List<String> keywords = new ArrayList<>();
        keywords.add(null);
        keywords.add("a");

        assertThrows(IllegalArgumentException.class,
                () -> newsClient.getAllNews(keywords, "fake", "a"),
                "Keyword in keywords in parameter cannot be null");
    }
}
