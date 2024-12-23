package bg.sofia.uni.fmi.mjt.goodreads.book;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {
    @Test
    public void testBookOfNullTokens() {
        assertThrows(IllegalArgumentException.class, () -> Book.of(null),
                "Null tokens must throw an exception");
    }

    @Test
    public void testBookOfNullID() {
        String[] tokens = {null, "Gatsby", "F. Scott", "Alternate", "['Classics']", "3.93", "39,642", "https:"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Null id token must throw an exception");
    }

    @Test
    public void testBookOfNullTitle() {
        String[] tokens = {"7", null, "F. Scott", "Alternate", "['Classics']", "3.93", "39,642", "https:"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Null title token must throw an exception");
    }

    @Test
    public void testBookOfNullAuthor() {
        String[] tokens = {"7", "Gatsby", null, "Alternate", "['Classics']", "3.93", "39,642", "https:"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Null author token must throw an exception");
    }

    @Test
    public void testBookOfNullDescription() {
        String[] tokens = {"7", "Gatsby", "F. Scott", null, "['Classics']", "3.93", "39,642", "https:"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Null description token must throw an exception");
    }

    @Test
    public void testBookOfNullGenres() {
        String[] tokens = {"7", "Gatsby", "F. Scott", "Alternate", null, "3.93", "39,642", "https:"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Null genres token must throw an exception");
    }

    @Test
    public void testBookOfNullRating() {
        String[] tokens = {"7", "Gatsby", "F. Scott", "Alternate", "['Classics']", null, "39,642", "https:"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Null rating token must throw an exception");
    }

    @Test
    public void testBookOfNegativeRating() {
        String[] tokens = {"7", "Gatsby", "F. Scott", "Alternate", "['Classics']", "-0.01", "39,642", "https:"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Negative rating token must throw an exception");
    }

    @Test
    public void testBookOfNullRatingCount() {
        String[] tokens = {"7", "Gatsby", "F. Scott", "Alternate", "['Classics']", "3.93", null, "https:"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Null rating count token must throw an exception");
    }

    @Test
    public void testBookOfNegativeRatingCount() {
        String[] tokens = {"7", "Gatsby", "F. Scott", "Alternate", "['Classics']", "3.93", "-1", "https:"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Negative rating count token must throw an exception");
    }

    @Test
    public void testBookOfNullUrl() {
        String[] tokens = {"7", "Gatsby", "F. Scott", "Alternate", "['Classics']", "3.93", "39,642", null};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Null url token must throw an exception");
    }

    @Test
    public void testBookOfInvalidCountOfParameters() {
        String[] tokens = {"7", "Gatsby", "F. Scott", "Alternate", "['Classics']", "3.93", "39,642", "https:", "oneMore"};
        assertThrows(IllegalArgumentException.class, () -> Book.of(tokens),
                "Throws exception when count of tokens does not equal the count of parameters");
    }

    @Test
    public void testBookOfValid() {
        String[] tokens = {"7", "Gatsby", "F. Scott", "Alternate", "['Classics']", "3.93", "39,642", "https:"};
        assertDoesNotThrow(() -> Book.of(tokens), "Should not throw exception with correct tokens values");
    }

    @Test
    public void testBookOfGenresSmallLetters() {
        String[] tokens = {"7", "Gatsby", "F. Scott", "Alternate", "['CLASSICS']", "3.93", "39,642", "https:"};
        assertEquals("classics", Book.of(tokens).genres().getFirst(), "Genres should be saved with small letters");
    }

    @Test
    public void testBookOfGenresTrimmedSymbolsExceptDash() {
        String[] tokens = {"7", "Gatsby", "F. Scott", "Alternate", "['{{classics}}']", "3.93", "39,642", "https:"};
        assertEquals("classics", Book.of(tokens).genres().getFirst(),
                "Genres should be saved and trimmed without symbols except dash");

        String[] tokens2 = {"7", "Gatsby", "F. Scott", "Alternate", "['hip-hop']", "3.93", "39,642", "https:"};
        assertEquals("hip-hop", Book.of(tokens2).genres().getFirst(),
                "Genres should be saved and trimmed without symbols except dash");
    }
}
