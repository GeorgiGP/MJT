package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidMapGrid;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidValue;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoPathRestaurantClient;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoSuchDelivery;
import bg.sofia.uni.fmi.mjt.glovo.validation.Validations;

import java.util.LinkedList;
import java.util.Queue;

public class ControlCenter implements ControlCenterApi {
    private final MapEntity[][] map;
    int rows;
    int cols;
    public static final int NOT_LIMIT = -1;
    private static final double EPSILON = 1e-9;

    public ControlCenter(char[][] mapLayout) {
        if (mapLayout == null) {
            throw new InvalidMapGrid("Map layout is null");
        }
        rows = mapLayout.length;
        if (mapLayout[0] == null) {
            throw new InvalidMapGrid("Map layout row is null");
        }
        cols = mapLayout[0].length;
        map = new MapEntity[rows][cols];

        for (int i = 0; i < rows; i++) {
            if (mapLayout[i] == null) {
                throw new InvalidMapGrid("Map layout row is null");
            }
            for (int j = 0; j < map[i].length; j++) {
                if (map[i].length != cols) {
                    throw new InvalidMapGrid("Invalid map grid size. Each row should be the same length.");
                }
                Location location = new Location(i, j);
                MapEntityType type = MapEntityType.fromSymbol(mapLayout[i][j]);
                if (type == null) {
                    throw new InvalidMapGrid("Invalid map symbol: " + mapLayout[i][j]);
                }
                map[i][j] = new MapEntity(location, type);
            }
        }
    }

    private void validateLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Locations can't be null.");
        }
        if (location.x() < 0 || location.x() >= rows || location.y() < 0 || location.y() >= cols) {
            throw new IllegalArgumentException("Invalid location: " + location);
        }
    }

    @Override
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation,
                                               double maxPrice, int maxTime, ShippingMethod shippingMethod) {
        validateLocation(restaurantLocation);
        validateLocation(clientLocation);
        if (shippingMethod == null) {
            throw new IllegalArgumentException("Shipping can't be null.");
        }
        Validations.validateRestaurant(map[restaurantLocation.x()][restaurantLocation.y()]);
        Validations.validateClient(map[clientLocation.x()][clientLocation.y()]);
        if (maxTime <= 0 && maxTime != NOT_LIMIT) {
            throw new InvalidValue("Impossible to deliver for non positive time");
        }
        if ((Math.abs(maxPrice) < EPSILON || maxPrice < 0) && Math.abs(maxPrice - NOT_LIMIT) >= EPSILON) {
            throw new InvalidValue("Impossible to deliver for non positive price");
        }
        int kmRestaurantClient = findMinKmClientRestaurant(restaurantLocation, clientLocation);
        if (kmRestaurantClient == NOT_LIMIT) {
            throw new NoPathRestaurantClient("There does not exist any path between the client and restaurant.");
        }
        if (shippingMethod == ShippingMethod.CHEAPEST) {
            return findCheapestDeliveryGuy(restaurantLocation, kmRestaurantClient, maxPrice, maxTime);
        } else {
            return findFastestDeliveryGuy(restaurantLocation, kmRestaurantClient, maxPrice, maxTime);
        }
    }

    @Override
    public MapEntity[][] getLayout() {
        MapEntity[][] result = new MapEntity[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            result[i] = map[i].clone();
        } //MapEntity is immutable so we can make this array shallow copy
        return result;
    }

    private int findMinKmClientRestaurant(Location restaurantLocation, Location clientLocation) {
        Queue<Location> queue = new LinkedList<>();
        boolean[][] wentTrough = new boolean[rows][cols];
        int km = 0;
        queue.add(restaurantLocation);
        int size = 1;
        while (!queue.isEmpty()) {
            for (int i = 0; i < size; i++) {
                if (queue.isEmpty()) {
                    return -1;
                }
                Location location = queue.poll();
                if (location.equals(clientLocation)) {
                    return km;
                }
                addNeighbours(location, wentTrough, queue);
            }
            size = queue.size();
            km++;
        } return -1;
    }

    private void addNeighbours(Location location, boolean[][] wentTrough, Queue<Location> layers) {
        wentTrough[location.x()][location.y()] = true;
        if (location.y() != 0 && map[location.x()][location.y() - 1].type() != MapEntityType.WALL
                && !wentTrough[location.x()][location.y() - 1]) {
            layers.add(map[location.x()][location.y() - 1].location());
        }
        if (location.y() != cols - 1 && map[location.x()][location.y() + 1].type() != MapEntityType.WALL
                && !wentTrough[location.x()][location.y() + 1]) {
            layers.add(map[location.x()][location.y() + 1].location());
        }
        if (location.x() != 0 && map[location.x() - 1][location.y()].type() != MapEntityType.WALL
                && !wentTrough[location.x() - 1][location.y()]) {
            layers.add(map[location.x() - 1][location.y()].location());
        }
        if (location.x() != rows - 1 && map[location.x() + 1][location.y()].type() != MapEntityType.WALL
                && !wentTrough[location.x() + 1][location.y()]) {
            layers.add(map[location.x() + 1][location.y()].location());
        }
    }

    private boolean inLimit(int kmToRestaurant, int kmFromRestaurantToClient,
                                 DeliveryType deliveryType, double maxPrice, int time) {
        return inTimeLimit(kmToRestaurant, kmFromRestaurantToClient, deliveryType, time) &&
                inPriceLimit(kmToRestaurant, kmFromRestaurantToClient, deliveryType, maxPrice);
    }

    private boolean inTimeLimit(int kmToRestaurant, int kmFromRestaurantToClient,
                                DeliveryType deliveryType, int maxTime) {
        if (maxTime == NOT_LIMIT) {
            return true;
        }
        return ((kmToRestaurant + kmFromRestaurantToClient) * deliveryType.getTimeKilometer() <= maxTime);
    }

    private boolean inPriceLimit(int kmToRestaurant, int kmFromRestaurantToClient,
                                 DeliveryType deliveryType, double maxPrice) {
        if (Math.abs(maxPrice - NOT_LIMIT) < EPSILON) {
            return true;
        }
        double calculated = (kmToRestaurant + kmFromRestaurantToClient) * deliveryType.getPriceKilometer();
        return (Math.abs(calculated - maxPrice) < EPSILON || calculated < maxPrice);
    }

    private DeliveryInfo buildDeliveryInfo(MapEntity deliveryGuy, int kilometersGuyRestaurant,
                                           int kilometersRestaurantClient, double maxPrice, int maxTime) {
        if (deliveryGuy == null)
            return null; //plan B candidate was not found either
        DeliveryType type = switch (deliveryGuy.type()) {
            case DELIVERY_GUY_CAR -> DeliveryType.CAR;
            case DELIVERY_GUY_BIKE -> DeliveryType.BIKE;
            default -> throw new NoSuchDelivery("Only implemented with car and bike deliveries");
        };
        if (inLimit(kilometersGuyRestaurant, kilometersRestaurantClient, type, maxPrice, maxTime)) {
            int sumKm = kilometersGuyRestaurant + kilometersRestaurantClient;
            int price = type.getPriceKilometer() * sumKm;
            int time = type.getTimeKilometer() * sumKm;
            return new DeliveryInfo(deliveryGuy.location(), price, time, type);
        } else {
            return null;
        }

    }

    private DeliveryInfo buildValidDeliveryInfo(MapEntity deliveryGuy, int kilometersGuyRestaurant,
                                           int kilometersRestaurantClient) {
        if (deliveryGuy == null)
            return null; // just in case
        DeliveryType type = switch (deliveryGuy.type()) {
            case DELIVERY_GUY_CAR -> DeliveryType.CAR;
            case DELIVERY_GUY_BIKE -> DeliveryType.BIKE;
            default -> throw new NoSuchDelivery("Only implemented with car and bike deliveries");
        };
        int sumKm = kilometersGuyRestaurant + kilometersRestaurantClient;
        int price = type.getPriceKilometer() * sumKm;
        int time = type.getTimeKilometer() * sumKm;
        return new DeliveryInfo(deliveryGuy.location(), price, time, type);
    }

    private int findLimitKilometersGuyRestaurant(double maxPrice, int maxTime, int kmRestaurantClient) {
        return Math.min(findPriceLimitFactor(maxPrice),
                findTimeLimitFactor(maxTime)) - kmRestaurantClient;
    }

    private int findTimeLimitFactor(int maxTime) {
        if (maxTime == NOT_LIMIT) {
            return Integer.MAX_VALUE;
        }
        return maxTime / DeliveryType.CAR.getTimeKilometer();
    }

    private int findPriceLimitFactor(double maxPrice) {
        if (Math.abs(maxPrice - NOT_LIMIT) < EPSILON) {
            return Integer.MAX_VALUE;
        }
        return (int)(maxPrice / DeliveryType.BIKE.getPriceKilometer());
    }

    /**
     * @return a new limit of kilometers that restricts bike guy, there is a new kilometer range that is meaningful to
     * search in if we find a bike guy for potential better price delivery
     */
    private int setTimeOutForBikeGuy(int currentLimitKm, int carGuySucceedKilometers, int kmRestaurantClient) {
        return Math.min(currentLimitKm,
                ((kmRestaurantClient + carGuySucceedKilometers) * DeliveryType.CAR.getPriceKilometer()
                / DeliveryType.BIKE.getPriceKilometer() - kmRestaurantClient));
    }

    private DeliveryInfo findCheapestDeliveryGuy(Location restaurantLocation, int kmRestaurantClient,
                                                 double maxPrice, int maxTime) {
        boolean[][] wentTrough = new boolean[rows][cols];
        MapEntity candidate = null; //Nearest car driver, Plan B if no bike nearby
        int kmWhenCandidateWins = 0;
        int km = 0;
        boolean isBikeInLimit = true; //to calculate only for first one and not multiple times
        boolean isCarInLimit = true; //to calculate only for first one and not multiple times
        int limitKm = findLimitKilometersGuyRestaurant(maxPrice, maxTime, kmRestaurantClient);
        Queue<Location> queue = new LinkedList<>();
        queue.add(restaurantLocation);
        int size = 1; //each count of candidates in a layer of bfs
        while (!queue.isEmpty() && km <= limitKm) {
            for (int i = 0; i < size && !queue.isEmpty(); i++) {
                if (!isBikeInLimit && !isCarInLimit) return null;
                Location location = queue.poll();
                MapEntity current = map[location.x()][location.y()];
                if (current.type() == MapEntityType.DELIVERY_GUY_CAR && isCarInLimit && candidate == null &&
                        (isCarInLimit = inLimit(km, kmRestaurantClient, DeliveryType.CAR, maxPrice, maxTime))) {
                    candidate = current;
                    limitKm = setTimeOutForBikeGuy(limitKm, km, kmRestaurantClient);
                    kmWhenCandidateWins = km;
                } else if (current.type() == MapEntityType.DELIVERY_GUY_BIKE && isBikeInLimit &&
                        (isBikeInLimit = inLimit(km, kmRestaurantClient, DeliveryType.BIKE, maxPrice, maxTime))) {
                    return buildValidDeliveryInfo(current, kmRestaurantClient, km);
                }
                addNeighbours(location, wentTrough, queue);
            } size = queue.size();
            km++;
        } return buildDeliveryInfo(candidate, kmRestaurantClient, kmWhenCandidateWins, maxPrice, maxTime);
    }

    /**
     * @return a new limit of kilometers that restricts car guy, there is a new kilometer range that is meaningful to
     * search in if we find a car guy for potential better time delivery
     */
    private int setTimeOutForCarGuy(int currentLimitKm, int bikeGuySucceedKilometers, int kmRestaurantClient) {
        return Math.min(currentLimitKm,
                ((kmRestaurantClient + bikeGuySucceedKilometers) * DeliveryType.BIKE.getTimeKilometer()
                        / DeliveryType.CAR.getTimeKilometer() - kmRestaurantClient));
    }

    private DeliveryInfo findFastestDeliveryGuy(Location restaurantLocation, int kmRestaurantClient,
                                                double maxPrice, int maxTime) {
        boolean[][] wentTrough = new boolean[rows][cols];
        MapEntity candidate = null; //Nearest bike driver, Plan B if no car nearby
        int kmWhenCandidateWins = 0;
        int km = 0;
        boolean isBikeInLimit = true; //to calculate only for first one and not multiple times
        boolean isCarInLimit = true; //to calculate only for first one and not multiple times
        int limitKm = findLimitKilometersGuyRestaurant(maxPrice, maxTime, kmRestaurantClient);
        Queue<Location> queue = new LinkedList<>();
        queue.add(restaurantLocation);
        int size = 1; //each count of candidates in a layer of bfs
        while (!queue.isEmpty() && km <= limitKm) {
            for (int i = 0; i < size && !queue.isEmpty(); i++) {
                if (!isBikeInLimit && !isCarInLimit) return null;
                Location location = queue.poll();
                MapEntity current = map[location.x()][location.y()];
                if (current.type() == MapEntityType.DELIVERY_GUY_BIKE && isBikeInLimit && candidate == null &&
                        (isBikeInLimit = inLimit(km, kmRestaurantClient, DeliveryType.BIKE, maxPrice, maxTime))) {
                    candidate = current;
                    limitKm = setTimeOutForCarGuy(limitKm, km, kmRestaurantClient);
                    kmWhenCandidateWins = km;
                } else if (current.type() == MapEntityType.DELIVERY_GUY_CAR && isCarInLimit &&
                        (isCarInLimit = inLimit(km, kmRestaurantClient, DeliveryType.CAR, maxPrice, maxTime))) {
                    return buildValidDeliveryInfo(current, kmRestaurantClient, km);
                }
                addNeighbours(location, wentTrough, queue);
            } size = queue.size();
            km++;
        } return buildDeliveryInfo(candidate, kmRestaurantClient, kmWhenCandidateWins, maxPrice, maxTime);
    }
}
