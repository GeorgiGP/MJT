package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DLC extends StoreItemInfo{
    private Game game;

    /**
     @throws NullPointerException if game is null
     */
    public DLC(String title, BigDecimal price, LocalDateTime releaseDate, Game game) {
        super(title, price, releaseDate);
        if (game == null) {
            throw new NullPointerException("Game given in constructor is null");
        }
        this.game = game;
    }
}
