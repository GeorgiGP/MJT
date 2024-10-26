package bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter;

import java.math.BigDecimal;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.category.StoreItemInfo;
public class PriceItemFilter implements ItemFilter {
    private final BigDecimal lowerBoundPrice;
    private final BigDecimal upperBoundPrice;

    public PriceItemFilter(BigDecimal lowerBound, BigDecimal upperBound) {
        if (!StoreItemInfo.validatePrice(lowerBound) || !StoreItemInfo.validatePrice(upperBound)) {
            throw new IllegalArgumentException("When choosing filter with price select correct price number!");
        }
        lowerBoundPrice = lowerBound;
        upperBoundPrice = upperBound;
    }

    @Override
    public boolean matches(StoreItem item) {
        BigDecimal price = item.getPrice();
        return lowerBoundPrice.compareTo(price) <= 0 && price.compareTo(upperBoundPrice) <= 0;
    }
}
