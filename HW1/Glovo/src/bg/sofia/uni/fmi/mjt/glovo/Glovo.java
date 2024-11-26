package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenter;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenterApi;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.Delivery;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidOrderException;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoAvailableDeliveryGuyException;

public class Glovo implements GlovoApi {
    private final ControlCenterApi controlCenter;

    public static final int NO_CONSTRAINT = -1;

    public Glovo(char[][] mapLayout) {
        controlCenter = new ControlCenter(mapLayout);
    }

    @Override
    public Delivery getCheapestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
            throws NoAvailableDeliveryGuyException {
        return getCheapestDeliveryWithinTimeLimit(client, restaurant, foodItem, NO_CONSTRAINT);
    }

    @Override
    public Delivery getFastestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
            throws NoAvailableDeliveryGuyException {
        return getFastestDeliveryUnderPrice(client, restaurant, foodItem, NO_CONSTRAINT);
    }

    @Override
    public Delivery getFastestDeliveryUnderPrice(MapEntity client, MapEntity restaurant,
                                                 String foodItem, double maxPrice)
            throws NoAvailableDeliveryGuyException {
        validateRequest(client, restaurant, foodItem);
        DeliveryInfo found = controlCenter
                .findOptimalDeliveryGuy(restaurant.location(), client.location(), maxPrice,
                        NO_CONSTRAINT, ShippingMethod.FASTEST);

        if (found == null) {
            throw new NoAvailableDeliveryGuyException(
                    "No available delivery guy found for delivery.");
        }

        return new Delivery(client.location(), restaurant.location(), found.deliveryGuyLocation(),
                foodItem, found.price(), found.estimatedTime());
    }

    @Override
    public Delivery getCheapestDeliveryWithinTimeLimit(MapEntity client, MapEntity restaurant,
                                                       String foodItem, int maxTime)
            throws NoAvailableDeliveryGuyException {
        validateRequest(client, restaurant, foodItem);
        DeliveryInfo found = controlCenter
                .findOptimalDeliveryGuy(restaurant.location(), client.location(), NO_CONSTRAINT,
                        maxTime, ShippingMethod.CHEAPEST);

        if (found == null) {
            throw new NoAvailableDeliveryGuyException(
                    "No available delivery guy found for delivery.");
        }

        return new Delivery(client.location(), restaurant.location(), found.deliveryGuyLocation(),
                foodItem, found.price(), found.estimatedTime());
    }

    /**
     * @param client map entity from type client
     * @throws IllegalArgumentException if client, restaurant or foodItem is null
     * @throws InvalidOrderException    if client not client type or restaurant not restaurant type
     */
    private void validateRequest(MapEntity client, MapEntity restaurant, String foodItem) {
        if (foodItem == null) {
            throw new IllegalArgumentException("FoodItem cannot be null");
        }
        validateClient(client);
        validateRestaurant(restaurant);
    }

    /**
     * @param client map entity from type client
     * @throws IllegalArgumentException if null
     * @throws InvalidOrderException    if not client
     */
    private void validateClient(MapEntity client) {
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
    private void validateRestaurant(MapEntity restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
        if (restaurant.type() != MapEntityType.RESTAURANT) {
            throw new InvalidOrderException("Restaurant not the required restaurant type");
        }
    }
}
