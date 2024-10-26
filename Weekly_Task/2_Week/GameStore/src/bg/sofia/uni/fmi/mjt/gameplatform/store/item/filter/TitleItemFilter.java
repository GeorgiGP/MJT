package bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

public class TitleItemFilter implements ItemFilter {
    private String search;
    private final boolean caseSensitive;
    /**
     @throws IllegalArgumentException if rating is invalid
     */
    public TitleItemFilter(String title, boolean caseSensitive) {
        if (title == null) {
            throw new NullPointerException("search is null");
        }
        if (title.isEmpty()) {
            throw new IllegalArgumentException("search is empty");
        }
        this.search = title;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean matches(StoreItem item) {
        String itemTitle = item.getTitle();
        if (!this.caseSensitive) {
            search = search.toLowerCase();
            itemTitle = itemTitle.toLowerCase();
        }
        return itemTitle.contains(search);
    }
}
