package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TFIDFSimilarityCalculator implements SimilarityCalculator {
    private final TextTokenizer tokenizer;

    private final Set<Set<String>> tokenizedDescriptionsSavedBooks;
    private final Map<Book, Map<String, Integer>> tokenizedDescriptionsWordsPerBook;
    private final Map<String, Integer> wordsCountDescriptionOccurrences;

    public TFIDFSimilarityCalculator(Set<Book> books, TextTokenizer tokenizer) {
        if (books == null || tokenizer == null || books.isEmpty()) {
            throw new IllegalArgumentException("Invalid data");
        }
        this.tokenizer = tokenizer;
        tokenizedDescriptionsWordsPerBook = new ConcurrentHashMap<>();
        tokenizedDescriptionsSavedBooks = books.parallelStream()
                .map(this::getUniqueWordsPerBook)
                .collect(Collectors.toSet());

        wordsCountDescriptionOccurrences = new HashMap<>();
        setUpWordOccurrences();
    }

    private void setUpWordOccurrences() {
        tokenizedDescriptionsSavedBooks.forEach(set ->
                set.forEach(word ->
                        wordsCountDescriptionOccurrences.merge(word, 1, Integer::sum)
                )
        );
    }

    /*
     * Do not modify!
     */
    @Override
    public double calculateSimilarity(Book first, Book second) {
        Map<String, Double> tfIdfScoresFirst = computeTFIDF(first);
        Map<String, Double> tfIdfScoresSecond = computeTFIDF(second);

        return cosineSimilarity(tfIdfScoresFirst, tfIdfScoresSecond);
    }

    public Map<String, Double> computeTFIDF(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        Map<String, Double> tfWords = computeTF(book);
        Map<String, Double> idfWords = computeIDF(book);

        return tfWords.entrySet()
                .parallelStream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() * idfWords.get(entry.getKey())
                ));
    }

    public Map<String, Double> computeTF(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        Map<String, Integer> wordsCount = getUniqueWordsCountPerBook(book);
        int totalWords = wordsCount.values().parallelStream().mapToInt(Integer::intValue).sum();

        return wordsCount.entrySet()
                .parallelStream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> (double) entry.getValue() / totalWords
                ));
    }

    private double calculateIdfSingleWord(String word) {
        if (!wordsCountDescriptionOccurrences.containsKey(word)) {
            return 0;
        }
        long occurs = wordsCountDescriptionOccurrences.get(word);
        return Math.log10((double) tokenizedDescriptionsSavedBooks.size() / occurs);
    }

    public Map<String, Double> computeIDF(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        Set<String> words = getUniqueWordsPerBook(book);
        return words.parallelStream()
                .collect(Collectors.toMap(
                        word -> word,
                        this::calculateIdfSingleWord
                ));
    }

    private Set<String> getUniqueWordsPerBook(Book book) {
        return getUniqueWordsCountPerBook(book).keySet();
    }

    private Map<String, Integer> getUniqueWordsCountPerBook(Book book) {
        return tokenizedDescriptionsWordsPerBook.computeIfAbsent(book, b -> {
            List<String> essentialWords = tokenizer.tokenize(b.description());
            return essentialWords.parallelStream()
                    .collect(Collectors.groupingBy(word -> word, Collectors.summingInt(word -> 1)));
        });
    }

    private double cosineSimilarity(Map<String, Double> first, Map<String, Double> second) {
        double magnitudeFirst = magnitude(first.values());
        double magnitudeSecond = magnitude(second.values());

        return dotProduct(first, second) / (magnitudeFirst * magnitudeSecond);
    }

    private double dotProduct(Map<String, Double> first, Map<String, Double> second) {
        Set<String> commonKeys = new HashSet<>(first.keySet());
        commonKeys.retainAll(second.keySet());

        return commonKeys.stream()
                .mapToDouble(word -> first.get(word) * second.get(word))
                .sum();
    }

    private double magnitude(Collection<Double> input) {
        double squaredMagnitude = input.stream()
                .map(v -> v * v)
                .reduce(0.0, Double::sum);

        return Math.sqrt(squaredMagnitude);
    }
}
