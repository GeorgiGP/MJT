package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.HashSet;
import java.util.Set;

public class GenresOverlapSimilarityCalculator implements SimilarityCalculator {

    @Override
    public double calculateSimilarity(Book first, Book second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Books must not be null");
        }
        Set<String> genresBook1 = new HashSet<>(first.genres());
        Set<String> genresBook2 = new HashSet<>(second.genres());
        Set<String> intersection = new HashSet<>(genresBook1);
        intersection.retainAll(genresBook2);
        int minSize = Math.min(genresBook1.size(), genresBook2.size());
        if (minSize == 0) {
            return 0;
        }
        return (double) intersection.size() / minSize;
    }
    
}
