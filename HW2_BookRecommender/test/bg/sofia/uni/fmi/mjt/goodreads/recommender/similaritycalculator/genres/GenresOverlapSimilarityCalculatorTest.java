package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GenresOverlapSimilarityCalculatorTest {
    private static List<Book> books;

    private static GenresOverlapSimilarityCalculator calculator;

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

        calculator = new GenresOverlapSimilarityCalculator();
    }

    @Test
    void testCalculateSimilarityValid() {
        assertEquals(0.5, calculator.calculateSimilarity(books.get(0), books.get(1)));
        assertEquals(1, calculator.calculateSimilarity(books.get(1), books.get(2)));
    }

    @Test
    void testCalculateSimilarityZeroGenresBook() {
        Book zeroGenres = new Book("id3", "The Book2", "Author2", "no genres book",
                List.of(), 3.2, 2, "https");
        assertEquals(0.0, calculator.calculateSimilarity(books.getFirst(), zeroGenres),
                "If a book has no genres, then their similarity should be 0.0");
    }

    @Test
    void testCalculateSimilarityNullFirstBook() {
        assertThrows(IllegalArgumentException.class,() -> calculator.calculateSimilarity(null, books.get(1)),
                "When a book is null when calculating similarities, exception should be thrown");
    }

    @Test
    void testCalculateSimilarityNullSecondBook() {
        assertThrows(IllegalArgumentException.class,() -> calculator.calculateSimilarity(books.getFirst(), null),
                "When a book is null when calculating similarities, exception should be thrown");
    }
}
