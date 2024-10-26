package bg.sofia.uni.fmi.mjt.gameplatform.store;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter.ItemFilter;

import java.math.BigDecimal;

public class GameStore implements StoreAPI{
    private final StoreItem[] items;
    private boolean discounted100yo = false;
    private boolean discountedVan40 = false;

    /**
     @throws NullPointerException if availableItems is null
     @throws IllegalArgumentException if availableItems is empty
     */
    public GameStore(StoreItem[] availableItems) {
        if (availableItems == null) {
            throw new NullPointerException("availableItems is null");
        }
        if (availableItems.length == 0) {
            throw new IllegalArgumentException("availableItems is empty");
        }
        items = availableItems;
    }

    /**
     @throws NullPointerException if itemFilters is null
     */
    public StoreItem[] findItemByFilters(ItemFilter[] itemFilters) {
        if (itemFilters == null) {
            throw new NullPointerException("itemFilters is null");
        }
        if (itemFilters.length == 0) {
            return items;
        }

        StoreItem[] foundItems = new StoreItem[itemFilters.length];
        int countFoundItems = 0;
        for (StoreItem item : items) {
            boolean found = true;
            for (ItemFilter itemFilter : itemFilters) {
                if (!itemFilter.matches(item)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                foundItems[countFoundItems++] = item;
            }
        }
        StoreItem[] filteredItems = new StoreItem[countFoundItems];
        System.arraycopy(foundItems, 0, filteredItems, 0, countFoundItems);
        return filteredItems;
    }

    public void applyDiscount(String promoCode) {
        if (discounted100yo) {
            return;
        }
        if (promoCode.equals("100YO")) {
            BigDecimal zeroPrice = BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_HALF_UP);
            for(StoreItem item : items) {
                item.setPrice(zeroPrice);

            }
            discounted100yo = true;
        } else if (promoCode.equals("VAN40")) {
            if (discountedVan40) {
                return;
            }
            for(StoreItem item : items) {
                BigDecimal price = item.getPrice();
                BigDecimal multiplier = BigDecimal.valueOf(0.60).setScale(2, BigDecimal.ROUND_HALF_UP);
                price = price.multiply(multiplier).setScale(2, BigDecimal.ROUND_HALF_UP);
                item.setPrice(price);
            }
            discountedVan40 = true;
        }
    }

    /**
     * Rates a store item.
     *
     * @param item the item to be rated
     * @param rating the rating to be given in the range [1, 5]
     * @return true if the item is successfully rated, false otherwise
     */
    public boolean rateItem(StoreItem item, int rating) {
        try {
            item.rate(rating);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            return false;
        }
    }
}
