package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.lang.Math.abs;

public abstract class StoreItemInfo implements StoreItem {
    private String title;
    private BigDecimal price;
    private double rating;
    private int countRatings;
    private LocalDateTime releaseDate;

    public static final double EPSILON = 0.000001;
    public static final double MIN_RATING = 1;
    public static final double MAX_RATING = 5;

    public static boolean validateRating(double rating) {
        return ((MIN_RATING < rating) && (rating < MAX_RATING)) || (abs(rating - MAX_RATING) < EPSILON) || (abs(rating - MIN_RATING) < EPSILON);
    }

    public static boolean validatePrice(BigDecimal price) {

        BigDecimal mustPrice = price.setScale(2, BigDecimal.ROUND_HALF_UP);
        return abs(mustPrice.doubleValue() - price.doubleValue()) < EPSILON && price.doubleValue() >= 0;
    }

    public StoreItemInfo(String title, BigDecimal price, LocalDateTime releaseDate) {
        setTitle(title);
        setPrice(price);
        setReleaseDate(releaseDate);
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(BigDecimal price) {
        if (!validatePrice(price)) {
            throw new IllegalArgumentException("Price must be number with 2 digits after decimal point");
        }
        this.price = price.setScale(2, BigDecimal.ROUND_HALF_UP);;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void rate(double rating) {
        if (!validateRating(rating)) {
            throw new IllegalArgumentException("Invalid rating");
        }
        this.rating = (this.rating * (countRatings++) + rating) / countRatings;
    }
}
