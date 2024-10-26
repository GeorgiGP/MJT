package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Game extends StoreItemInfo {
    private String genre;

    public Game(String title, BigDecimal price, LocalDateTime releaseDate, String genre) {
        super(title, price, releaseDate);
        this.genre = genre;
    }
}
