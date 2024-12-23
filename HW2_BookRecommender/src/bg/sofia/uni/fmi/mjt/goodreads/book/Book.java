package bg.sofia.uni.fmi.mjt.goodreads.book;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record Book(
        String ID,
        String title,
        String author,
        String description,
        List<String> genres,
        double rating,
        int ratingCount,
        String URL
) {
    private static final int COUNT_OF_PARAMETERS = 8;

    public Book {
        if (ID == null || title == null || author == null ||
                description == null || genres == null || URL == null) {
            throw new IllegalArgumentException("Info in book cannot be null");
        }
        if (rating < 0 || ratingCount < 0) {
            throw new IllegalArgumentException("Rating and rating count in book cannot be negative");
        }
    }

    public static Book of(String[] tokens) {
        if (tokens == null) {
            throw new IllegalArgumentException("tokens is null");
        }
        if (tokens.length != COUNT_OF_PARAMETERS) {
            throw new IllegalArgumentException("Invalid csv format for book");
        }
        if (Arrays.stream(tokens).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Info in book cannot be null");
        }
        int index = 0;
        String id = tokens[index++];
        String title = tokens[index++];
        String author = tokens[index++];
        String description = tokens[index++];

        List<String> genres = genresTokenize(tokens[index++]);
        double rating = Double.parseDouble(tokens[index++]);
        int ratingCount = ratingCountTokenize(tokens[index++]);
        String url = tokens[index];

        return new Book(id, title, author, description, genres, rating, ratingCount, url);
    }

    private static List<String> genresTokenize(String token) {
        return Arrays.stream(token
                        .replaceAll("[\\p{Punct}&&[^,-]]", "")
                        .replaceAll("\\s+", " ")
                        .strip()
                        .toLowerCase()
                        .split(","))
                .toList();
    }

    private static int ratingCountTokenize(String token) {
        return Integer.parseInt(token.replaceAll("[\\p{Punct}&&[^-]]", ""));
    }
}
