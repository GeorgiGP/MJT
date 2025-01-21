package bg.sofia.uni.fmi.mjt.newsapi.newsdata;

import com.google.gson.annotations.SerializedName;

public record NewsArticle(@SerializedName("author") String author,
                         @SerializedName("title") String title,
                         String description,
                         String content) { }
