import bg.sofia.uni.fmi.mjt.gameplatform.store.GameStore;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.category.Game;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        LocalDateTime t = LocalDateTime.now();
        BigDecimal deca = BigDecimal.valueOf(1.50);
        StoreItem i1 = new Game("Text", deca, t, "as");
        StoreItem i2 = new Game("Text", deca, t, "as");
        StoreItem i3 = new Game("Text", deca, t, "as");
        StoreItem[] l = {i1, i2, i3};
        GameStore g = new GameStore(l);
        //g.applyDiscount("100yo");
    }
}