package bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

import java.time.LocalDateTime;

public class ReleaseDateItemFilter implements ItemFilter {
    private final LocalDateTime lowerBoundDate;
    private final LocalDateTime upperBoundDate;

    public ReleaseDateItemFilter(LocalDateTime lowerBound, LocalDateTime upperBound) {
        lowerBoundDate = lowerBound;
        upperBoundDate = upperBound;
    }

    @Override
    public boolean matches(StoreItem item) {
        LocalDateTime releaseDate = item.getReleaseDate();
        return lowerBoundDate.isBefore(releaseDate) && releaseDate.isBefore(upperBoundDate);
    }
}
