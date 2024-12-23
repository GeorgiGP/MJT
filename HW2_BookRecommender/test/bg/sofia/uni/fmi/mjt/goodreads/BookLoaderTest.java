package bg.sofia.uni.fmi.mjt.goodreads;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookLoaderTest {
    private static StringReader reader;
    private static List<Book> books;

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
        books.add(new Book("id3", "The Book3", "Author2", "a random one", genres, 3.2, 2, "https"));

        reader = new StringReader("id1,The Book,Author,\"a book for cooking\",\"['genre1','genre2']\", 2, \"4\",https" + System.lineSeparator() +
                                     "id1,The Book,Author,\"a book for cooking\",\"['genre1','genre2']\", 2, \"4\",https" + System.lineSeparator() +
                                     "id2,The Book2,Author2,\"a book 2\", \"['genre1','genre2']\", 3.2, \"2\",https" + System.lineSeparator() +
                                     "id3,The Book3,Author2,\"a random one\", \"['genre1','genre2']\", 3.2, \"2\",https");
    }

    @Test
    void testLoadValid() {
        assertEquals(new HashSet<>(books), BookLoader.load(reader));
    }
}
