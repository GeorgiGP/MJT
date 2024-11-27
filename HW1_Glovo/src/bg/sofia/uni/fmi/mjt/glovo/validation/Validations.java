package bg.sofia.uni.fmi.mjt.glovo.validation;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidOrderException;

public class Validations {
    /**
     * @param client map entity from type client
     * @throws IllegalArgumentException if null
     * @throws InvalidOrderException    if not client
     */
    public static void validateClient(MapEntity client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (client.type() != MapEntityType.CLIENT) {
            throw new InvalidOrderException("Client not the required client type");
        }
    }

    /**
     * @param restaurant map entity from type restaurant
     * @throws IllegalArgumentException if null
     * @throws InvalidOrderException    if not client
     */
    public static void validateRestaurant(MapEntity restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
        if (restaurant.type() != MapEntityType.RESTAURANT) {
            throw new InvalidOrderException("Restaurant not the required restaurant type");
        }
    }

}
