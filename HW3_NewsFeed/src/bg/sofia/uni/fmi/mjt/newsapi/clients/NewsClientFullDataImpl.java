package bg.sofia.uni.fmi.mjt.newsapi.clients;

import bg.sofia.uni.fmi.mjt.newsapi.newsdata.NewsArticle;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewsClientFullDataImpl extends NewsClient implements NewsClientFullData {
    private static final int MAX_CONSUMERS = 5;

    public NewsClientFullDataImpl(HttpClient newsHttpClient, String apiKey) {
        super(newsHttpClient, apiKey);
    }

    /**
     * Requires always keywords, otherwise server will return error
     *
     * @return list of all articles per criteria.
     * Important! If the articles are more than MAX_NEWS_COUNT_TO_RECEIVE, then the function
     * returns MAX_NEWS_COUNT_TO_RECEIVE of them, because of subscription plan.
     */
    @Override
    public List<NewsArticle> getAllNews(List<String> keywords, String country,
                                        String category) {
        if (keywords == null || country == null || category == null ||
                keywords.parallelStream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Category, country, keywords and words in keywords cannot be null");
        }
        List<NewsArticle> result = new Vector<>();

        String formatedKeywords = String.join(CONCAT_SYMBOL, keywords);
        int countOfNews = getNewsCount(formatedKeywords, country, category);
        countOfNews = Math.min(countOfNews, MAX_NEWS_COUNT_TO_RECEIVE);

        int pages = (int) Math.ceil((double)countOfNews / MAX_PAGE_SIZE);

        try (ExecutorService executor = Executors.newFixedThreadPool(MAX_CONSUMERS)) {
            for (int i = 1; i <= pages; i++) {
                final int idx = i;
                executor.execute(() -> {
                    var response = responseFromNewsApi(formatedKeywords, country, category, MAX_PAGE_SIZE, idx);
                    result.addAll(GSON.fromJson(response.body(), News.class).articles());
                });
            }
            executor.shutdown();
        }
        return result;
    }

    @Override
    public List<NewsArticle> getNewsCountry(List<String> keywords, String country) {
        return getAllNews(keywords, country, "");
    }

    @Override
    public List<NewsArticle> getNewsCategory(List<String> keywords, String category) {
        return getAllNews(keywords, "", category);
    }

    @Override
    public List<NewsArticle> getNews(List<String> keywords) {
        return getAllNews(keywords, "", "");
    }
}
