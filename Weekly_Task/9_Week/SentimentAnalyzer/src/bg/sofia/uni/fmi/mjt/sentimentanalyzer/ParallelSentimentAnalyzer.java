package bg.sofia.uni.fmi.mjt.sentimentanalyzer;

import bg.sofia.uni.fmi.mjt.sentimentanalyzer.exceptions.SentimentAnalysisException;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelSentimentAnalyzer implements SentimentAnalyzerAPI {
    private final int consumerThreadsCount;
    private final Set<String> stopWords;
    private final Map<String, SentimentScore> sentimentLexicon;

    private static final int MAX_WAITING_MS = 100;
    /**
     * @param workersCount number of consumer workers
     * @param stopWords set containing stop words
     * @param sentimentLexicon map containing the sentiment lexicon,
     *                         where the key is the word and the value is the sentiment score
     */
    public ParallelSentimentAnalyzer(int workersCount, Set<String> stopWords,
                                     Map<String, SentimentScore> sentimentLexicon) {
        consumerThreadsCount = workersCount;
        this.stopWords = stopWords;
        this.sentimentLexicon = sentimentLexicon;
    }

    @Override
    public Map<String, SentimentScore> analyze(AnalyzerInput... inputs) {
        Map<String, SentimentScore> results = new ConcurrentHashMap<>();
        MyBlockingQueue<AnalyzerInput> taskQueue = new MyBlockingQueue<>(inputs.length);

        try (ExecutorService producerPool = Executors.newSingleThreadExecutor();
             ExecutorService consumerPool = Executors.newFixedThreadPool(consumerThreadsCount)) {

            for (AnalyzerInput input : inputs) {
                producerPool.execute(() -> produceTasks(taskQueue, results, input));
            }
            for (int i = 0; i < consumerThreadsCount; i++) {
                consumerPool.execute(() -> consumeTasks(taskQueue, results, producerPool));
            }

            producerPool.shutdown();
            consumerPool.shutdown();
        }
        return results;
    }

    private void produceTasks(MyBlockingQueue<AnalyzerInput> taskQueue, Map<String, SentimentScore> results,
                              AnalyzerInput input) {
        try {
            if (results.containsKey(input.inputID())) {
                throw new SentimentAnalysisException("One ID can't have two or more contexts!");
            }
            taskQueue.put(input);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void consumeTasks(MyBlockingQueue<AnalyzerInput> taskQueue, Map<String, SentimentScore> results,
                              ExecutorService producerPool) {
        while (!producerPool.isTerminated() || !taskQueue.isEmpty()) {
            AnalyzerInput input = taskQueue.poll(); // Retrieve task or timeout
            if (input != null) {
                SentimentScore score = calculateSentimentScore(input.inputReader());
                results.put(input.inputID(), score);
            }
        }
    }

    private SentimentScore calculateSentimentScore(Reader input) {
        String s = (new BufferedReader(input)).lines().reduce("", (line1, line2) -> line1 + " " + line2);
        String[] words = s.replaceAll("[\\p{Punct}&&[^']]", "").toLowerCase().split("\\s+");
        OptionalDouble score = Arrays.stream(words)
                .parallel()
                .filter(word -> !stopWords.contains(word) && sentimentLexicon.containsKey(word))
                .mapToInt(word -> sentimentLexicon.get(word).getScore())
                .average();
        double presence = score.isPresent() ? score.getAsDouble() : 0;
        return SentimentScore.fromScore(
                Math.clamp(Math.round(presence),
                        SentimentScore.VERY_NEGATIVE.getScore(),
                        SentimentScore.VERY_POSITIVE.getScore()));
    }
}
 