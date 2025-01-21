package bg.sofia.uni.fmi.mjt.newsapi;

import bg.sofia.uni.fmi.mjt.newsapi.clients.NewsClientFullData;
import bg.sofia.uni.fmi.mjt.newsapi.clients.NewsClientFullDataImpl;
import bg.sofia.uni.fmi.mjt.newsapi.clients.iterator.NewsClientIterablePages;
import bg.sofia.uni.fmi.mjt.newsapi.newsdata.NewsArticle;

import java.net.http.HttpClient;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        HttpClient httpClient = HttpClient.newHttpClient();

        String country = ""; // Replace with the city of your choice
        String content = ""; // Replace with the city of your choice

        NewsClientFullData newsClient = new NewsClientFullDataImpl(httpClient, "08b84a2176f046ba886b13d29ce2b3ef");

        long start = System.nanoTime();
        var news = newsClient.getAllNews( List.of("a"), country, content);
        long end = System.nanoTime();

        for (NewsArticle newsArticles : news) {
            System.out.println();
            System.out.println(newsArticles);
        }

        System.out.println((end - start) / 1000000 + "ms");

        NewsClientIterablePages newsClient2 = new NewsClientIterablePages(httpClient,
        "08b84a2176f046ba886b13d29ce2b3ef",
                30, List.of("ab"), country, content);

        long start2 = System.nanoTime();
        for (List<NewsArticle> newsArticles : newsClient2) {
            System.out.println();
            System.out.println(newsArticles.size());
            for (NewsArticle newsArticle : newsArticles) {
                System.out.println(newsArticle);
            }
        }
        long end2 = System.nanoTime();
        System.out.println((end2 - start2) / 1000000 + "ms");

    }
}
