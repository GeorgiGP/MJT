package bg.sofia.uni.fmi.mjt.sentimentanalyzer;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CriteriaAnalyzerLoader {
    public static Set<String> getStopwords(Reader input) {
        BufferedReader reader = new BufferedReader(input);
        return reader.lines().collect(Collectors.toSet());
    }

    public static Map<String, SentimentScore> getSentimentLexicon(Reader input) {
        BufferedReader reader = new BufferedReader(input);
        return reader.lines()
                .collect(Collectors.toMap(
                        line -> line.replaceFirst("\\s+\\S+$", "").strip(),
                        line -> SentimentScore.fromScore(Integer.parseInt(getLastWord(line)))
                ));
    }

    private static String getLastWord(String input) {
        String[] words = input.trim().split("\\s+");
        return words[words.length - 1];
    }
}
