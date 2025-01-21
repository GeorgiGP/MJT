package bg.sofia.uni.fmi.mjt.newsapi.clients;

import bg.sofia.uni.fmi.mjt.newsapi.newsdata.NewsArticle;
import java.util.List;

public interface NewsClientFullData {
    List<NewsArticle> getAllNews(List<String> keywords, String country, String category);

    List<NewsArticle> getNewsCountry(List<String> keywords, String country);

    List<NewsArticle> getNewsCategory(List<String> keywords, String category);

    List<NewsArticle> getNews(List<String> keywords);
}
