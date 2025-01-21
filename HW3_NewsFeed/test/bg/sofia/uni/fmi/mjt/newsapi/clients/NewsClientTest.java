package bg.sofia.uni.fmi.mjt.newsapi.clients;

import bg.sofia.uni.fmi.mjt.newsapi.exceptions.NewsClientException;
import bg.sofia.uni.fmi.mjt.newsapi.exceptions.RequestArgumentsException;
import bg.sofia.uni.fmi.mjt.newsapi.exceptions.RequestKeyException;
import bg.sofia.uni.fmi.mjt.newsapi.exceptions.ResponseServerError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewsClientTest {
    private static final String newsBody =
            """
                    {
                      "status": "ok",
                      "totalResults": 2,
                      "articles": [
                        {
                          "source": {
                            "id": "el-mundo",
                            "name": "El Mundo"
                          },
                          "author": null,
                          "title": "Sï¿œnchez advierte contra la \\"tecnocasta\\" ante la vuelta de Trump a la Casa Blanca y afirma que Europa debe \\"plantar cara a esta amenaza\\"",
                          "description": "Pedro Sï¿œnchez ha activado el botï¿œn de alarma ante la vuelta de Donald Trump a la Casa Blanca con el apoyo de Elon Musk. \\"La historia, si nos ha mostrado algo, es que las...",
                          "url": "https://www.elmundo.es/espana/2025/01/20/678e1f9afc6c83ba268b457e.html",
                          "urlToImage": "https://phantom-elmundo.unidadeditorial.es/9075694a3ab20ddcf5df34edc87d44e7/resize/1200/f/webp/assets/multimedia/imagenes/2025/01/20/17373719016765.jpg",
                          "publishedAt": "2025-01-20T11:58:04Z",
                          "content": "Pedro Sï¿nchez ha activado el botï¿n de alarma ante la vuelta de Donald Trump a la Casa Blanca con el apoyo de Elon Musk. \\"La historia, si nos ha mostrado algo, es que las tecnologï¿as no generan pro… [+1134 chars]"
                        },
                        {
                          "source": {
                            "id": "bild",
                            "name": "Bild"
                          },
                          "author": "BILD",
                          "title": "Raub in Krefeld: Gruppe attackiert Mann und stiehlt Smartphone | Regional",
                          "description": "Fünf junge Männer rauben ein Smartphone in Krefeld. Polizei findet das Gerät und die Kopfhörer dank Spürhund.",
                          "url": "https://www.bild.de/regional/nordrhein-westfalen/raub-in-krefeld-gruppe-attackiert-mann-und-stiehlt-smartphone-678e39bab086b06da79a2f28",
                          "urlToImage": "https://images.bild.de/678e39bab086b06da79a2f28/3310b570fdf4619dc4dd78e9424f1b06,29c59ff9?w=1280",
                          "publishedAt": "2025-01-20T11:57:55Z",
                          "content": "Krefeld Eine Gruppe junger Männer hat in der Nacht zum Samstag auf der Straße «An der Rennbahn» einen Mann überfallen und dabei sein Smartphone und seine Kopfhörer gestohlen.\\r\\nDie Polizei konnte im Z… [+844 chars]"
                        }]
                    }
                    """;

    @Mock
    private static HttpClient newsHttpClient = mock();

    @Mock
    private static HttpResponse<String> response = mock();

    @BeforeAll
    public static void setUp() {
        when(response.body()).thenReturn(newsBody);
    }

    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        when(newsHttpClient.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenReturn(response);
    }

    @Test
    void testCountValid() {
        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        NewsClient client = new NewsClientFullDataImpl(newsHttpClient, "123");
        assertEquals(client.getNewsCount("fake", "fake", "fake"), 2,
                "When totalResults equals 2 in the body, when getNewsCount method should return 2");
    }

    @Test
    void testResponseFromNewsApiInvalidKeyWithCodeHTTP_UNAUTHORIZED() {
        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);

        NewsClient client = new NewsClientFullDataImpl(newsHttpClient, "123");
        assertThrows(RequestKeyException.class,
                () -> client.responseFromNewsApi("fake", "fake", "fake", 1, 1),
                "When HTTP_UNAUTHORIZED (" + HttpURLConnection.HTTP_UNAUTHORIZED + ") code is returned, " +
                        "then should throw a RequestKeyException");
    }

    @Test
    void testResponseFromNewsApiInvalidArgumentsWithCodeHTTP_BAD_REQUEST() {
        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);

        NewsClient client = new NewsClientFullDataImpl(newsHttpClient, "123");
        assertThrows(RequestArgumentsException.class,
                () -> client.responseFromNewsApi("fake", "fake", "fake", 1, 1),
                "When HTTP_BAD_REQUEST (" + HttpURLConnection.HTTP_BAD_REQUEST + ") code is returned, " +
                        "then should throw a RequestArgumentsException");
    }

    @Test
    void testResponseFromNewsApiInvalidServerWithCodeHTTP_INTERNAL_ERROR() {
        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);

        NewsClient client = new NewsClientFullDataImpl(newsHttpClient, "123");
        assertThrows(ResponseServerError.class,
                () -> client.responseFromNewsApi("fake", "fake", "fake", 1, 1),
                "When HTTP_INTERNAL_ERROR (" + HttpURLConnection.HTTP_INTERNAL_ERROR + ") code is returned, " +
                        "then should throw a ResponseServerException");
    }

    @Test
    void testResponseFromNewsApiInvalidWithUnknownErrorCode() {
        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_PROXY_AUTH);

        NewsClient client = new NewsClientFullDataImpl(newsHttpClient, "123");
        assertThrows(NewsClientException.class,
                () -> client.responseFromNewsApi("fake", "fake", "fake", 1, 1),
                "When unknown error code is returned, then should throw a NewsClientException");
    }

    @Test
    void testResponseFromNewsApiInvalidWithUnknownException() throws IOException, InterruptedException {
        when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(newsHttpClient.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())).thenThrow(IllegalArgumentException.class);

        NewsClient client = new NewsClientFullDataImpl(newsHttpClient, "123");
        assertThrows(NewsClientException.class,
                () -> client.responseFromNewsApi("fake", "fake", "fake", 1, 1),
                "When unknown exception is thrown when searching for response in responseFromNewsApi method, " +
                        "then should throw a NewsClientException");
    }
}
