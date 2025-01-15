package bg.sofia.uni.fmi.mjt.sentimentanalyzer;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CriteriaAnalyzerLoaderTest {
    @Test
    void testGetStopwords() {
        Reader stopwordsInput = new StringReader("i" + System.lineSeparator() +
                "playing");
        assertEquals(CriteriaAnalyzerLoader.getStopwords(stopwordsInput), Set.of("i", "playing"));
    }

    @Test
    void testGetSentimentLexicon() {
        Reader sentimentLexicon = new StringReader("love 3" + System.lineSeparator() +
                "hate -4");
        assertEquals(CriteriaAnalyzerLoader.getSentimentLexicon(sentimentLexicon),
                Map.of("love", SentimentScore.fromScore(3), "hate", SentimentScore.fromScore(-4)));
    }
}
