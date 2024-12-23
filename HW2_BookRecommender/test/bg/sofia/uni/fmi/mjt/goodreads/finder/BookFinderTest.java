package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookFinderTest {
    private static final TextTokenizer tokenizer = mock();
    private static List<Book> books;

    private static BookFinder finder;

    @BeforeAll
    public static void setUp() {
        books = new ArrayList<>();

        List<String> genres;
        genres = new ArrayList<>();
        genres.add("genre1");
        genres.add("genre2");
        books = new ArrayList<>();
        books.add(new Book("id1", "The Book", "Author", "a book for cooking pasta", genres, 2, 4, "https"));

        genres = new ArrayList<>();
        genres.add("genre3");
        genres.add("genre2");
        books.add(new Book("id2", "The Book2", "Author2", "a book 2", genres, 3.2, 2, "https"));

        finder = new BookFinder(new HashSet<>(books), tokenizer);
        when(tokenizer.tokenize(books.getFirst().description())).thenReturn(List.of("book", "cooking", "pasta"));
        when(tokenizer.tokenize(books.getLast().description())).thenReturn(List.of("book", "2"));
    }

    @Test
    public void testBookFinderNullTokenizer() {
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(new HashSet<>(books), null),
                "Should throw exception if tokenizer is null");
    }

    @Test
    public void testBookFinderNullBooks() {
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(null, tokenizer),
                "Should throw exception if set of books is null");
    }

    @Test
    public void testAllBooksUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> finder.allBooks().clear(),
                "Should throw exception if changing through all books reference");
    }

    @Test void testAllBooksByAuthorValid() {
        List<Book> searchByAuthor = finder.searchByAuthor("Author");
        assertEquals(1, searchByAuthor.size(),
                "Should return 1 element if the author has only 1 book in the set of books");
        assertEquals(books.getFirst(), searchByAuthor.getFirst());

        List<Book> searchByAuthor2 = finder.searchByAuthor("Author2");
        assertEquals(1, searchByAuthor2.size(),
                "Should return 1 element if the author has only 1 book in the set of books");
        assertEquals(books.getLast(), searchByAuthor2.getLast());
    }

    @Test
    void testAllBooksByAuthorNull() {
        assertThrows(IllegalArgumentException.class, () -> finder.searchByAuthor(null),
                "Should throw exception if searching by author is null");
    }

    @Test
    void testAllGenres() {
        assertEquals(Set.of("genre1", "genre2", "genre3"), finder.allGenres(),
                "Should return the set with all unique genres of all books small letters");
    }

    @Test
    void testSearchByGenresMatchingAll() {
        Set<String> genres = Set.of("genre2", "genre1");
        List<Book> searchedByGenres = finder.searchByGenres(genres, MatchOption.MATCH_ALL);
        assertEquals(1, searchedByGenres.size(),
                "Should return 1 element if one book has genres 1 2, other has 2 3, " +
                "and we search book matching 1 and 2");
        assertEquals(books.getFirst(), searchedByGenres.getFirst(),
                "Should return first element if one book has genres 1 2, other has 2 3, " +
                "and we search book matching 1 and 2");
    }

    @Test
    void testSearchByGenresMatchingAny() {
        Set<String> genres = Set.of("genre2", "genre1");
        List<Book> searchedByGenres = finder.searchByGenres(genres, MatchOption.MATCH_ANY);
        assertEquals(2, searchedByGenres.size(),
                "Should return 2 element if one book has genres 1 2, other has 2 3, " +
                "and we search book matching 1 or 2");
        assertEquals(new HashSet<>(searchedByGenres), new HashSet<>(books),
                "Should return all element if one book has genres 1 2, other has 2 3, " +
                                              "and we search book matching 1 and 2");
    }

    @Test
    void testSearchByKeywordsMatchingAll() {
        List<Book> searchedBooksByKeyword = finder.searchByKeywords(Set.of("book", "pasta"), MatchOption.MATCH_ALL);
        assertEquals(1, searchedBooksByKeyword.size(),
                "Should return 1 element if one book has keywords description \"book, cooking, pasta\", other has \"book, 2\"" +
                "and we search book matching both: \"book, pasta\"");
        assertEquals(books.getFirst(), searchedBooksByKeyword.getFirst(),
                "Should return first element if first book has keywords description \"book, cooking, pasta\", other has \"book, 2\"" +
                "and we search book matching both: \"book, pasta\"");
    }

    @Test
    void testSearchByKeywordsMatchingAny() {
        List<Book> searchedBooksByKeyword = finder.searchByKeywords(Set.of("book", "pasta"), MatchOption.MATCH_ANY);
        assertEquals(2, searchedBooksByKeyword.size(),
                "Should return 2 element if one book has keywords description \"book, cooking, pasta\", other has \"book, 2\"" +
                "and we search book matching any of: \"book, pasta\"");
        assertEquals(new HashSet<>(searchedBooksByKeyword), new HashSet<>(books),
                "Should return first element if first book has keywords description \"book, cooking, pasta\", other has \"book, 2\"" +
                "and we search book matching both: \"book, pasta\"");
    }
}
