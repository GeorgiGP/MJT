import bg.sofia.uni.fmi.mjt.sentimentanalyzer.AnalyzerInput;
import bg.sofia.uni.fmi.mjt.sentimentanalyzer.CriteriaAnalyzerLoader;
import bg.sofia.uni.fmi.mjt.sentimentanalyzer.ParallelSentimentAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        ParallelSentimentAnalyzer p = new ParallelSentimentAnalyzer(10,
                CriteriaAnalyzerLoader.getStopwords(new FileReader(new File("resources/stopwords.txt"))),
                CriteriaAnalyzerLoader.getSentimentLexicon(new FileReader(new File("resources/AFINN-111.txt"))));
        String[] inputs = {
                "I love programming, it's so wow. But sometimes I hate when the code doesn't work.",
                "Today was a good day. I felt happy and accomplished, though I had some challenges.",
                "I feel so sad today. Everything seems bad and nothing is going right.",
                "I love working on new projects. However, I hate the pressure of deadlines.",
                "Life is good. I am happy with my work and personal life.",
                "The weather is nice today, which makes me feel good. I love sunny days.",
                "I feel bad about the mistakes I made yesterday. It's tough to fix things.",
                "Hate is such a strong word. It's better to focus on good things.",
                "Good things come to those who wait. I am confident about the future.",
                "Sad to see my friends leave, but I know they will be successful in their new journey.",
                "Sad to see my friends leave, but I know they will be successful in their new journey."
        };

        AnalyzerInput[] analyzerInputs = new AnalyzerInput[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            analyzerInputs[i] = new AnalyzerInput("ID-" + i, new StringReader(inputs[i]));
        }

        long start = System.nanoTime();
        System.out.println(p.analyze(analyzerInputs));
        long end = System.nanoTime();
        System.out.println((end - start) / 1000000 + "ms");
    }
}
