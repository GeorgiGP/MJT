package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookRecommenderTest {
    private final static SimilarityCalculator mockCalculator = mock();

    private static List<Book> books;
    private static Book recommendFrom;
    private static BookRecommender recommender;

    @BeforeAll
    public static void setUp() {
        books = new ArrayList<>();

        List<String> genres;
        genres = new ArrayList<>();
        genres.add("genre1");
        genres.add("genre2");
        books = new ArrayList<>();
        books.add(new Book("id1", "The Book", "Author", "a book for cooking", genres, 2, 4, "https"));

        genres = new ArrayList<>();
        genres.add("genre3");
        genres.add("genre2");
        books.add(new Book("id2", "The Book2", "Author2", "a book for baking", genres, 3.2, 2, "https"));

        recommendFrom = new Book("id3", "Some book", "Author3",
                "description doesn't matter here, genres matter only", genres, 3.2, 2, "https");

        when(mockCalculator.calculateSimilarity(books.get(0), recommendFrom)).thenReturn(0.5);
        when(mockCalculator.calculateSimilarity(books.get(1), recommendFrom)).thenReturn(1.0);

        recommender = new BookRecommender(new HashSet<>(books), mockCalculator);
    }

    @Test
    void testBookRecommenderConstructorNullSet() {
        assertThrows(IllegalArgumentException.class,
                () -> new BookRecommender(null, mockCalculator),
                "When set is null should throw an exception");
    }

    @Test
    void testBookRecommenderConstructorEmptySet() {
        assertThrows(IllegalArgumentException.class,
                () -> new BookRecommender(Set.of(), mockCalculator),
                "When set is empty should throw an exception");
    }

    @Test
    void testBookRecommenderConstructorNullTokenizer() {
        assertThrows(IllegalArgumentException.class,
                () -> new BookRecommender(new HashSet<>(books), null),
                "When tokenizer is null should throw an exception");
    }

    @Test
    void testBookRecommenderRecommendValid() {
        SortedMap<Book, Double> recommendations = new TreeMap<>(
                (book1, book2) -> book2.ID().compareTo(book1.ID())
        );
        recommendations.put(books.get(0), 0.5);
        recommendations.put(books.get(1), 1.0);
        assertEquals(recommendations, recommender.recommendBooks(recommendFrom, 2),
                "Recommending should be sorted by similarity in descending order");
    }

    @Test
    void testBookRecommenderRecommendNullBook() {
        assertThrows(IllegalArgumentException.class, () -> recommender.recommendBooks(null, 1),
                "When book is null should throw an exception");
    }

    @Test
    void testBookRecommenderRecommendNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> recommender.recommendBooks(recommendFrom, -1),
                "When book is null should throw an exception");
    }
}
