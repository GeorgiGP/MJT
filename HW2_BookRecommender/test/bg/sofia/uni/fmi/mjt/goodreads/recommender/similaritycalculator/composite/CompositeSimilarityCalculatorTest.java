package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions.TFIDFSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres.GenresOverlapSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompositeSimilarityCalculatorTest {
    private static final TextTokenizer tokenizer = mock();
    private static List<Book> books;

    private static TFIDFSimilarityCalculator calculatorTfIdf;
    private static GenresOverlapSimilarityCalculator calculatorGenres;
    private static CompositeSimilarityCalculator calculatorComposite;

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
        books.add(new Book("id2", "The Book2", "Author2", "a book 2", genres, 3.2, 2, "https"));
        books.add(new Book("id3", "The Book2", "Author2", "a random one", genres, 3.2, 2, "https"));

        when(tokenizer.tokenize(books.getFirst().description())).thenReturn(List.of("book", "cooking", "pasta"));
        when(tokenizer.tokenize(books.get(1).description())).thenReturn(List.of("book", "2"));
        when(tokenizer.tokenize(books.getLast().description())).thenReturn(List.of("random"));
        calculatorTfIdf = new TFIDFSimilarityCalculator(new HashSet<>(books), tokenizer);
        calculatorGenres = new GenresOverlapSimilarityCalculator();

        Map<SimilarityCalculator, Double> mapWithEqualWeights = new HashMap<>();
        mapWithEqualWeights.put(calculatorTfIdf, 0.5);
        mapWithEqualWeights.put(calculatorGenres, 0.5);
        calculatorComposite = new CompositeSimilarityCalculator(mapWithEqualWeights);
    }

    @Test
    void testCompositeSimilarityCalculatorNullCalculatorMap() {
        assertThrows(IllegalArgumentException.class, () -> new CompositeSimilarityCalculator(null),
                "Map cannot be null");
    }

    @Test
    void testCalculateSimilarityValid() {
        assertEquals(0.08743110367262207, calculatorTfIdf.calculateSimilarity(books.get(0), books.get(1)));
        assertEquals(0.5, calculatorGenres.calculateSimilarity(books.get(0), books.get(1)));
        assertEquals(0.5 * 0.5 + 0.5 * 0.08743110367262207, calculatorComposite.calculateSimilarity(books.get(0), books.get(1)));
    }
}
