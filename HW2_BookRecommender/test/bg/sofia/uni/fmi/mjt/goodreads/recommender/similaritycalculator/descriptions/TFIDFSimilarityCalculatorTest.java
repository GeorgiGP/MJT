package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TFIDFSimilarityCalculatorTest {
    private static final TextTokenizer tokenizer = mock();
    private static List<Book> books;

    private static TFIDFSimilarityCalculator calculator;

    @BeforeAll
    public static void setUp() {
        books = new ArrayList<>();

        List<String> genres;
        genres = new ArrayList<>();
        genres.add("genre1");
        genres.add("genre2");

        books = new ArrayList<>();
        books.add(new Book("id1", "The Book", "Author", "a book for cooking", genres, 2, 4, "https"));
        books.add(new Book("id2", "The Book2", "Author2", "a book 2", genres, 3.2, 2, "https"));
        books.add(new Book("id3", "The Book2", "Author2", "a random one", genres, 3.2, 2, "https"));

        when(tokenizer.tokenize(books.getFirst().description())).thenReturn(List.of("book", "cooking", "pasta"));
        when(tokenizer.tokenize(books.get(1).description())).thenReturn(List.of("book", "2"));
        when(tokenizer.tokenize(books.getLast().description())).thenReturn(List.of("random"));
        calculator = new TFIDFSimilarityCalculator(new HashSet<>(books), tokenizer);
    }

    @Test
    void testCalculateSimilarityValid() {
        assertEquals(0.08743110367262207, calculator.calculateSimilarity(books.get(0), books.get(1)));
        assertEquals(0.0, calculator.calculateSimilarity(books.get(1), books.get(2)));
        assertEquals(0.0, calculator.calculateSimilarity(books.get(0), books.get(2)));
    }

    @Test
    void testComputeTfIdfNullBook() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.computeTFIDF(null),
                "When book is null should throw an exception");
    }

    @Test
    void testComputeTfNullBook() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.computeTF(null),
                "When book is null should throw an exception");
    }

    @Test
    void testComputeIdfNullBook() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.computeIDF(null),
                "When book is null should throw an exception");
    }

    @Test
    void testTfIdfSimilarityCalculatorConstructorNullSet() {
        assertThrows(IllegalArgumentException.class,
                () -> new TFIDFSimilarityCalculator(null, tokenizer),
                "When set is null should throw an exception");
    }

    @Test
    void testTfIdfSimilarityCalculatorConstructorEmptySet() {
        assertThrows(IllegalArgumentException.class,
                () -> new TFIDFSimilarityCalculator(Set.of(), tokenizer),
                "When set is empty should throw an exception");
    }

    @Test
    void testTfIdfSimilarityCalculatorConstructorNullTokenizer() {
        assertThrows(IllegalArgumentException.class,
                () -> new TFIDFSimilarityCalculator(new HashSet<>(books), null),
                "When tokenizer is null should throw an exception");
    }

}
