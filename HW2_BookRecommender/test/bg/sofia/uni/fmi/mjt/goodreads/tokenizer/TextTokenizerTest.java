package bg.sofia.uni.fmi.mjt.goodreads.tokenizer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TextTokenizerTest {
    private static TextTokenizer tokenizer;

    @BeforeAll
    public static void setUp() {
        StringReader reader = new StringReader("a" + System.lineSeparator() +
                                               "of" + System.lineSeparator() +
                                               "my" + System.lineSeparator() +
                                               "no");
        tokenizer = new TextTokenizer(reader);
    }

    @Test
    void testTextTokenizerConstructorGetterValidReading() {
        assertEquals(Set.of("a", "of", "my", "no"), tokenizer.stopwords(),
                "Getter of stopwords should return all read from the reader.");
    }

    @Test
    void testTextTokenizerNullInputTokenize() {
        assertThrows(IllegalArgumentException.class, () -> tokenizer.tokenize(null),
                "Tokenizing a null string should throw an exception.");
    }

    @Test
    void testTextTokenizerTokenizeValid() {
        assertEquals(List.of("book", "post"), tokenizer.tokenize("NO, A BOOk OF my post"),
                "Tokenize should return words in small letters and removing punctuations");
    }
}
