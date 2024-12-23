package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BookRecommender implements BookRecommenderAPI {
    private final Set<Book> books;
    private final SimilarityCalculator similarityCalculator;

    public BookRecommender(Set<Book> initialBooks, SimilarityCalculator calculator) {
        if (initialBooks == null || calculator == null || initialBooks.isEmpty()) {
            throw new IllegalArgumentException("Invalid input data for initialBooks or calculator");
        }
        this.books = initialBooks;
        this.similarityCalculator = calculator;
    }

    @Override
    public SortedMap<Book, Double> recommendBooks(Book origin, int maxN) {
        if (origin == null || maxN <= 0) {
            throw new IllegalArgumentException("Invalid input data for origin or maxN");
        }
        Map<Book, Double> cache = new HashMap<>();

        SortedMap<Book, Double> result = new TreeMap<>(new Comparator<Book>() {
            @Override
            public int compare(Book book1, Book book2) {
                double simularityB1 = getCacheCalculateSimilarity(book1, origin, cache);
                double simularityB2 = getCacheCalculateSimilarity(book2, origin, cache);
                int result = Double.compare(simularityB2, simularityB1);
                return result == 0 ? book1.ID().compareTo(book2.ID()) : result;
            }
        });

        for (Book book : books) {
            result.put(book, getCacheCalculateSimilarity(book, origin, cache));
        }
        return result.entrySet().parallelStream().limit(maxN).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (l, r) -> l,
                () -> new TreeMap<>(result.comparator())
        ));
    }

    private double getCacheCalculateSimilarity(Book book, Book origin, Map<Book, Double> cache) {
        return cache.computeIfAbsent(book, b -> similarityCalculator.calculateSimilarity(b, origin));
    }
}
