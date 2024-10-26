package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GameBundle extends StoreItemInfo{
    private Game[] games;

    /**
    @throws NullPointerException if games is null
    @throws IllegalArgumentException if games is empty
     */
    public GameBundle(String title, BigDecimal price, LocalDateTime releaseDate, Game[] games)
    {
        if(games == null) {
            throw new NullPointerException("Given list of games in constructor is null");
        }
        if(games.length == 0) {
            throw new IllegalArgumentException("Given list of games in constructor is empty");
        }
        super(title, price, releaseDate);
        this.games = games;
    }
}
