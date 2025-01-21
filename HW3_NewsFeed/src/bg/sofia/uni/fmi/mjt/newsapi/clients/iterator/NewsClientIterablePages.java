package bg.sofia.uni.fmi.mjt.newsapi.clients.iterator;

import bg.sofia.uni.fmi.mjt.newsapi.clients.NewsClient;
import bg.sofia.uni.fmi.mjt.newsapi.newsdata.NewsArticle;

import java.net.http.HttpClient;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class NewsClientIterablePages extends NewsClient implements Iterable<List<NewsArticle>> {
    private final int countArticlesPerPage;

    private final String formatedKeywords;
    private final String country;
    private final String category;

    private Integer countPages = null;
    private int countAllArticles;

    private class IteratorPage implements Iterator<List<NewsArticle>> {
        private int curPage = 1;

        @Override
        public boolean hasNext() {
            return curPage <= countPages;
        }

        @Override
        public List<NewsArticle> next() {
            int articlesToDisplay = (curPage == countPages) ?
                    countAllArticles % countArticlesPerPage : countArticlesPerPage;
            var response = responseFromNewsApi(formatedKeywords, country, category, articlesToDisplay, curPage++);
            return GSON.fromJson(response.body(), News.class).articles();
        }
    }

    public NewsClientIterablePages(HttpClient newsHttpClient, String apiKey, int countArticlesPerPage,
                                   List<String> keywords, String country, String category) {
        if (newsHttpClient == null || apiKey == null || keywords == null || country == null || category == null ||
                keywords.parallelStream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Parameters in constructor of NewsClientIterablePages and" +
                    "words in keywords should not be null");
        }
        if (countArticlesPerPage <= 0 || countArticlesPerPage > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException
                    ("Invalid number of articles per page, must be between 1 and " + MAX_PAGE_SIZE);
        }

        super(newsHttpClient, apiKey);
        this.countArticlesPerPage = countArticlesPerPage;
        this.formatedKeywords = String.join(CONCAT_SYMBOL, keywords);
        this.country = country;
        this.category = category;
    }

    public NewsClientIterablePages(HttpClient newsHttpClient, String apiKey, int countArticlesPerPage,
                                   List<String> keywords) {
        this(newsHttpClient, apiKey, countArticlesPerPage, keywords, "", "");
    }

    @Override
    public Iterator<List<NewsArticle>> iterator() {
        if (countPages == null) {
            countAllArticles = getNewsCount(formatedKeywords, country, category);
            countAllArticles = Math.min(MAX_NEWS_COUNT_TO_RECEIVE, countAllArticles);
            countPages = (int) Math.ceil(countAllArticles / (double) countArticlesPerPage);
        }
        return new IteratorPage();
    }
}
