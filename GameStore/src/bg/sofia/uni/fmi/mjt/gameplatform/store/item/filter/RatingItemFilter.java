package bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.category.StoreItemInfo;

public class RatingItemFilter implements ItemFilter {
    private final double rating;

    /**
     @throws IllegalArgumentException if rating is invalid
     */
    public RatingItemFilter(double rating) {
        if (!StoreItemInfo.validateRating(rating)) {
            throw new IllegalArgumentException("Invalid rating");
        }
        this.rating = rating;
    }

    @Override
    public boolean matches(StoreItem item) {
        return item.getRating() >= rating;
    }
}
