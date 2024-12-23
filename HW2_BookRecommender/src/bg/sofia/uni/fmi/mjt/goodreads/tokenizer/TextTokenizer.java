package bg.sofia.uni.fmi.mjt.goodreads.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TextTokenizer {
    
    private final Set<String> stopwords;

    public TextTokenizer(Reader stopwordsReader) {
        try (var br = new BufferedReader(stopwordsReader)) {
            stopwords = br.lines().collect(Collectors.toSet());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not load dataset", ex);
        }
    }

    public List<String> tokenize(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Null input");
        }

        input  = input.replaceAll("\\p{Punct}", "").strip().toLowerCase();
        String[] words = input.split("\\s+");
        return Arrays.stream(words).filter(word -> !stopwords.contains(word)).collect(Collectors.toList());
    }

    public Set<String> stopwords() {
        return stopwords;
    }
}
