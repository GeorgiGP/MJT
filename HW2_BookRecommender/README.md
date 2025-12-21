# Goodreads Book Recommender

Given a book, this project recommends the top N most similar books using statistical machine learning approaches. The recommendations are based on both book content and genres, leveraging natural language processing and similarity metrics.

## Features

* Recommend books based on description and title similarity (using TF-IDF and cosine similarity).
* Recommend books based on genre overlap.
* Support composite similarity, combining multiple similarity metrics with configurable weights.
* Uses stop-word filtering for better text processing.
* Easy to extend with new similarity calculators.

## Similarity Context

The recommender calculates similarity using:

* Genres – overlap of book genres.
* Textual content – TF-IDF on book titles and descriptions.
* Composite similarity – a weighted combination of multiple similarity metrics.

## Getting Started

### Prerequisites

* Java 17+
* Maven (for building the project)
* Input files:

  * `goodreads_data.csv` – dataset of books
  * `stopwords.txt` – list of stopwords

### Running the Application

1. Build the project:

```
mvn clean package
```

2. Run the main program:

```
java -cp target/your-jar-file.jar Main
```

3. The program loads the dataset, initializes similarity calculators, and prints the top N recommended books for an example book.

### Example Output

```
Book{title='Harry Potter and the Chamber of Secrets', author='J.K. Rowling'} -> 0.87
Book{title='Harry Potter and the Prisoner of Azkaban', author='J.K. Rowling'} -> 0.82
...
Execution Time: 153.4 ms
```

## Project Structure

* BookLoader – loads books from CSV.
* BookFinder – finds books from the dataset (optional).
* TextTokenizer – processes text, removing stopwords.
* Similarity Calculators – compute similarity between books:

  * TFIDFSimilarityCalculator – text-based similarity.
  * GenresOverlapSimilarityCalculator – genre-based similarity.
  * CompositeSimilarityCalculator – combination of multiple calculators.
* BookRecommender – provides top-N book recommendations using a similarity calculator.

## Extensibility

* Add new similarity calculators by implementing the SimilarityCalculator interface.
* Adjust weights in CompositeSimilarityCalculator for customized recommendations.
* Easily integrate new book datasets or stopword lists.

## License

MIT License
