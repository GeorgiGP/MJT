package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;

public class Main {
    public static void main(String[] args) {
        char[][] layout = {
                {'#', '#', '#', '.', 'C', 'A'},
                {'#', '#', 'B', 'R', '.', 'R'},
                {'A', '.', '.', '#', '#', '.'},
                {'#', 'C', '#', 'A', '.', 'R'},
                {'#', 'B', '.', '#', '.', 'A'},
                {'C', 'B', 'R', 'A', 'C', 'A'}
        };
        Location clientLocation = new Location(5, 4);
        Location restaurantLocation = new Location(1, 3);
        MapEntity client = new MapEntity(clientLocation, MapEntityType.CLIENT);
        MapEntity restaurant = new MapEntity(restaurantLocation, MapEntityType.RESTAURANT);
        GlovoApi glovo = new Glovo(layout);
        try {
            System.out.println(glovo.getCheapestDeliveryWithinTimeLimit(client, restaurant, "SUSHI", 30));
            System.out.println(glovo.getFastestDeliveryUnderPrice(client, restaurant, "PIZZA", 30));
            System.out.println(glovo.getCheapestDelivery(client, restaurant, "SPAGETTI"));
            System.out.println(glovo.getFastestDelivery(client, restaurant, "BURGER"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
