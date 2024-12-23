import bg.sofia.uni.fmi.mjt.goodreads.BookLoader;
import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.finder.BookFinder;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.BookRecommender;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite.CompositeSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions.TFIDFSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres.GenresOverlapSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.nanoTime();
        Set<Book> books = null;

        try (Reader reader = new FileReader("goodreads_data.csv")) {
            books = BookLoader.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TextTokenizer tokenizer = new TextTokenizer(new FileReader("stopwords.txt"));
        BookFinder bookFinder = new BookFinder(books, tokenizer);
        SimilarityCalculator calculator = new GenresOverlapSimilarityCalculator();
        SimilarityCalculator calculator1 = new TFIDFSimilarityCalculator(books, tokenizer);
        SimilarityCalculator calculator2 =
                new CompositeSimilarityCalculator(Map.of(calculator1, 1.2, calculator, 2.1));
        BookRecommender recommender = new BookRecommender(books, calculator1);
        Book b = Book.of(
                new String[] {"1127i124", "\"124124 Potter and the Philosopher’s Stone (Harry Potter, #1)\"", "J.K. Rowling",
                        "NotInTheDescriptionsOfTheReadedBooksForRecommendation owl, taken to Hogwarts School of Witchcraft and Wizardry, learns to play Quidditch and does battle in a deadly duel. The Reason ... HARRY POTTER IS A WIZARD!",
                        "['Fantasy', 'Fiction', 'Young Adult', 'Magic', 'Childrens', 'Middle Grade', 'Classics']", "4.47",
                        "9,278,135", "https://www.goodreads.com/book/show/72193.Harry_Potter_and_the_Philosopher_s_Stone"});

        Map<Book, Double> recomended = recommender.recommendBooks(b, 5);
        recomended.entrySet().forEach(System.out::println);

        long endTime = System.nanoTime();
        long durationInNanoseconds = endTime - startTime;
        double durationInMilliseconds = durationInNanoseconds / 1_000_000.0;
        System.out.println("Execution Time: " + durationInMilliseconds + " milliseconds");
    }
}
