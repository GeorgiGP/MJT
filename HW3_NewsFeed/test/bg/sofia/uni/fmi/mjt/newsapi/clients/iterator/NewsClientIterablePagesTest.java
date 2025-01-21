package bg.sofia.uni.fmi.mjt.newsapi.clients.iterator;

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

public class NewsClientIterablePagesTest {
    private static final String newsBodyCount =
            """
                    {
                      "status": "ok",
                      "totalResults": 3,
                      "articles": []
                    }
                    """;

    private static final String newsBody1 =
            """
                    {
                      "status": "ok",
                      "totalResults": 3,
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

    private static final String newsBody2 =
            """
                    {
                      "status": "ok",
                      "totalResults": 3,
                      "articles": [
                        {
                          "author": "author3",
                          "title": "title3",
                          "description": "description3",
                          "content": "content3"
                        }]
                    }
                    """;
    @Mock
    private static HttpClient newsHttpClient = mock();

    @Mock
    private static HttpResponse<String> response = mock();

    private static NewsClientIterablePages newsClient;

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        when(newsHttpClient.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(response);
        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        newsClient = new NewsClientIterablePages(newsHttpClient, "123", 2, List.of("test"),
                                                "test", "test");
    }

    @Test
    void testGetAllNewsValidListWith2Articles() {
        when(response.body()).thenReturn(newsBodyCount, newsBody1, newsBody2);

        List<NewsArticle> news = List.of(new NewsArticle("author", "title", "description", "content"),
                new NewsArticle("author2", "title2", "description2", "content2"));
        var it = newsClient.iterator();
        assertTrue(it.hasNext(), "When we have 3 articles to read, at the first page to be read iterator.hasNext should return true");
        assertIterableEquals(it.next(), news,
                "Those news were not expected. Should return: " + news);
        news = List.of(new NewsArticle("author3", "title3", "description3", "content3"));

        assertTrue(it.hasNext(), "When we have 3 articles to read, at the second page to be read iterator.hasNext should return true");
        assertIterableEquals(it.next(), news,
                "Those news were not expected. Should return: " + news);
        assertFalse(it.hasNext(), "Reading 3 articles wit 2 per page, at the third page hasNext should return false");
    }

    @Test
    void testNewsClientIterablePagesConstructorInvalidArticlesPerPageCountNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new NewsClientIterablePages(newsHttpClient, "",
                        -1, List.of("invalid"), "", ""));
    }

    @Test
    void testNewsClientIterablePagesConstructorInvalidArticlesPerPageCountTooBig() {
        assertThrows(IllegalArgumentException.class,
                () -> new NewsClientIterablePages(newsHttpClient, "",
                        1000, List.of("invalid"), "", ""));
    }

    @Test
    void testNewsClientIterablePagesConstructorNullKeywords() {
        assertThrows(IllegalArgumentException.class,
                () -> new NewsClientIterablePages(newsHttpClient, "key", 3,
                        null, "fake", "fake"), "Keywords in parameter cannot be null");
    }

    @Test
    void testNewsClientIterablePagesConstructorNullHttpClient() {
        assertThrows(IllegalArgumentException.class,
                () -> new NewsClientIterablePages(null, "key", 3,
                        List.of("fake"), "fake", "fake"), "HttpClient in parameter cannot be null");
    }

    @Test
    void testNewsClientIterablePagesConstructorNullKey() {
        assertThrows(IllegalArgumentException.class,
                () -> new NewsClientIterablePages(newsHttpClient, null, 3,
                        List.of("fake"), "fake", "fake"), "Key in parameter cannot be null");
    }

    @Test
    void testNewsClientIterablePagesConstructorNullKeyInKeywords() {
        List<String> keywords = new ArrayList<>();
        keywords.add(null);
        keywords.add("a");

        assertThrows(IllegalArgumentException.class,
                () -> new NewsClientIterablePages(newsHttpClient, "key", 3,
                        keywords, "fake", "fake"),
                "Key in keywords in parameter cannot be null");
    }

    @Test
    void testNewsClientIterablePagesConstructorNullCountry() {
        assertThrows(IllegalArgumentException.class,
                () -> new NewsClientIterablePages(newsHttpClient, "key", 3,
                        List.of("one"), null, "fake"),
                "Country in parameter cannot be null");
    }

    @Test
    void testNewsClientIterablePagesConstructorNullCategory() {
        assertThrows(IllegalArgumentException.class,
                () -> new NewsClientIterablePages(newsHttpClient, "key", 3,
                        List.of("one"), "fake", null),
                "Category in parameter cannot be null");
    }
}
