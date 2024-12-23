package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookFinder implements BookFinderAPI {
    private final Set<Book> books;
    private final TextTokenizer tokenizer;

    public BookFinder(Set<Book> books, TextTokenizer tokenizer) {
        if (books == null || tokenizer == null) {
            throw new IllegalArgumentException("Null books or tokenizer");
        }
        this.books = books;
        this.tokenizer = tokenizer;
    }

    @Override
    public Set<Book> allBooks() {
        return Collections.unmodifiableSet(books);
    }

    @Override
    public List<Book> searchByAuthor(String authorName) {
        if (authorName == null) {
            throw new IllegalArgumentException("Null author name");
        }
        return books.parallelStream().filter(book -> book.author().equals(authorName)).toList();
    }

    @Override
    public Set<String> allGenres() {
        return books.parallelStream().map(Book::genres).flatMap(List::stream).collect(Collectors.toSet());
    }

    @Override
    public List<Book> searchByGenres(Set<String> genres, MatchOption option) {
        switch (option) {
            case MATCH_ALL -> {
                return books.parallelStream().filter(book -> new HashSet<>(book.genres()).containsAll(genres)).toList();
            }
            case MATCH_ANY -> {
                return books.parallelStream()
                        .filter(book -> book.genres().parallelStream()
                                .anyMatch(genres::contains))
                        .toList();
            }
            default -> throw new IllegalArgumentException("Unknown match option: " + option);
        }
    }

    private boolean hasWords(Book book, Set<String> keywords, MatchOption option) {
        Set<String> words = new HashSet<>(tokenizer.tokenize(book.description()));
        words.addAll(tokenizer.tokenize(book.title()));
        switch (option) {
            case MATCH_ALL -> {
                return words.containsAll(keywords);
            }
            case MATCH_ANY -> {
                return words.parallelStream().anyMatch(keywords::contains);
            }
            default -> throw new IllegalArgumentException("Unknown match option: " + option);
        }
    }

    @Override
    public List<Book> searchByKeywords(Set<String> keywords, MatchOption option) {
        return books.parallelStream().filter(book -> hasWords(book, keywords, option)).toList();
    }
    
}
