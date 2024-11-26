package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidMapGrid;

import java.util.LinkedList;
import java.util.Queue;

public class ControlCenter implements ControlCenterApi {
    private final MapEntity[][] map;
    int rows;
    int cols;
    public static final int NOT_EXIST = -1;
    private static final double EPSILON = 1e-9;

    public ControlCenter(char[][] mapLayout) {
        rows = mapLayout.length;
        cols = mapLayout[0].length;
        map = new MapEntity[rows][cols];

        for (int i = 0; i < rows; i++) {
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

    @Override
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation,
                                               double maxPrice, int maxTime, ShippingMethod shippingMethod) {
        int kmRestaurantClient = findMinKmClientRestaurant(restaurantLocation, clientLocation);
        if (kmRestaurantClient == NOT_EXIST) {
            return null;
        }
        if (shippingMethod == ShippingMethod.CHEAPEST) {
            if (maxTime == NOT_EXIST) {
                maxTime = Integer.MAX_VALUE;
            }
            return findCheapestDeliveryGuy(restaurantLocation, kmRestaurantClient, maxTime);
        } else {
            if (Math.abs(maxPrice - NOT_EXIST) < EPSILON) {
                maxPrice = Double.POSITIVE_INFINITY;
            }
            return findFastestDeliveryGuy(restaurantLocation, kmRestaurantClient, maxPrice);
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

    private boolean inTimeLimit(int kmToRestaurant, int kmFromRestaurantToClient,
                                DeliveryType deliveryType, int maxTime) {
        return ((kmToRestaurant + kmFromRestaurantToClient) * deliveryType.getTimeKilometer() <= maxTime);
    }

    private boolean inPriceLimit(int kmToRestaurant, int kmFromRestaurantToClient,
                                 DeliveryType deliveryType, double maxPrice) {
        double calculated = (kmToRestaurant + kmFromRestaurantToClient) * deliveryType.getPriceKilometer();
        return (Math.abs(calculated - maxPrice) < EPSILON || calculated < maxPrice);
    }

    private DeliveryInfo buildDeliveryInfo(MapEntity deliveryGuy, int kilometersGuyRestaurant,
                                           int kilometersRestaurantClient) {
        if (deliveryGuy == null) {
            return null; //plan B candidate was not found either
        }
        DeliveryType type = switch (deliveryGuy.type()) {
            case DELIVERY_GUY_CAR -> DeliveryType.CAR;
            case DELIVERY_GUY_BIKE -> DeliveryType.BIKE;
            default -> null;
        };
        int sumKm = kilometersGuyRestaurant + kilometersRestaurantClient;
        int price = type.getPriceKilometer() * sumKm;
        int time = type.getTimeKilometer() * sumKm;
        return new DeliveryInfo(deliveryGuy.location(), price, time, type);
    }

    private DeliveryInfo buildDeliveryInfoForPrice(MapEntity deliveryGuy, int kilometersGuyRestaurant,
                                           int kilometersRestaurantClient, double maxPrice) {

        if (deliveryGuy == null) {
            return null; //plan B candidate was not found either
        }
        DeliveryType type = switch (deliveryGuy.type()) {
            case DELIVERY_GUY_CAR -> DeliveryType.CAR;
            case DELIVERY_GUY_BIKE -> DeliveryType.BIKE;
            default -> null;
        };
        if (!inPriceLimit(kilometersGuyRestaurant, kilometersRestaurantClient, type, maxPrice)) {
            return null;
        }
        int sumKm = kilometersGuyRestaurant + kilometersRestaurantClient;
        int price = type.getPriceKilometer() * sumKm;
        int time = type.getTimeKilometer() * sumKm;
        return new DeliveryInfo(deliveryGuy.location(), price, time, type);
    }

    private DeliveryInfo buildDeliveryInfoForTime(MapEntity deliveryGuy, int kilometersGuyRestaurant,
                                                   int kilometersRestaurantClient, int maxTime) {
        if (deliveryGuy == null) {
            return null; //plan B candidate was not found either
        }
        DeliveryType type = switch (deliveryGuy.type()) {
            case DELIVERY_GUY_CAR -> DeliveryType.CAR;
            case DELIVERY_GUY_BIKE -> DeliveryType.BIKE;
            default -> null;
        };
        if (!inTimeLimit(kilometersGuyRestaurant, kilometersRestaurantClient, type, maxTime)) {
            return null;
        }
        int sumKm = kilometersGuyRestaurant + kilometersRestaurantClient;
        int price = type.getPriceKilometer() * sumKm;
        int time = type.getTimeKilometer() * sumKm;
        return new DeliveryInfo(deliveryGuy.location(), price, time, type);
    }

    private int findTimeLimitFactor(int maxTime) {
        if (maxTime != NOT_EXIST) {
            return maxTime / DeliveryType.CAR.getTimeKilometer();
        }
        return Integer.MAX_VALUE;
    }

    private int findPriceLimitFactor(double maxPrice) {
        if (Math.abs(maxPrice - NOT_EXIST) < EPSILON) {
            return Integer.MAX_VALUE;
        }
        return (int)(maxPrice / DeliveryType.BIKE.getPriceKilometer());
    }

    private DeliveryInfo findCheapestDeliveryGuy(Location restaurantLocation, int kmRestaurantClient, int maxTime) {
        boolean[][] wentTrough = new boolean[rows][cols];
        MapEntity candidate = null; //Nearest car driver, Plan B if no bike nearby
        int kmWhenCandidateWins = 0;
        int km = 0;
        boolean isBikeInTimeLimit = true;
        int limit = findTimeLimitFactor(maxTime);
        Queue<Location> queue = new LinkedList<>();
        queue.add(restaurantLocation);
        int size = 1; //each count of candidates in a layer of bfs
        while (!queue.isEmpty() && km <= limit) {
            for (int i = 0; i < size && !queue.isEmpty() && km <= limit; i++) {
                Location location = queue.poll();
                MapEntity current = map[location.x()][location.y()];
                if (current.type() == MapEntityType.DELIVERY_GUY_CAR && candidate == null) {
                    candidate = current;
                    limit = Math.min(limit, ((kmRestaurantClient + km) * DeliveryType.CAR.getPriceKilometer()
                                    / DeliveryType.BIKE.getPriceKilometer() - kmRestaurantClient)); //to search for bike
                    kmWhenCandidateWins = km;
                }
                if (current.type() == MapEntityType.DELIVERY_GUY_BIKE && isBikeInTimeLimit &&
                        (isBikeInTimeLimit = inTimeLimit(km, kmRestaurantClient, DeliveryType.BIKE, maxTime))) {
                    return buildDeliveryInfo(current, kmRestaurantClient, km);
                }
                addNeighbours(location, wentTrough, queue);
            } size = queue.size();
            km++;
        }
        return buildDeliveryInfoForTime(candidate, kmRestaurantClient, kmWhenCandidateWins, maxTime);
    }

    private DeliveryInfo findFastestDeliveryGuy(Location restaurantLocation, int kmRestaurantClient, double maxPrice) {
        boolean[][] wentTrough = new boolean[rows][cols];
        MapEntity candidate = null; //Nearest bike driver, Plan B if no car nearby
        int kmWhenCandidateWins = 0;
        int km = 0;
        boolean isCarInPriceLimit = true;
        int limit = findPriceLimitFactor(maxPrice);
        Queue<Location> queue = new LinkedList<>();
        queue.add(restaurantLocation);
        int size = 1; //each count of candidates in a layer of bfs
        while (!queue.isEmpty() && km <= limit) {
            for (int i = 0; i < size && !queue.isEmpty() && km <= limit; i++) {
                Location location = queue.poll();
                MapEntity current = map[location.x()][location.y()];
                if (current.type() == MapEntityType.DELIVERY_GUY_BIKE && candidate == null) {
                    candidate = current;
                    limit = Math.min(limit, ((kmRestaurantClient + km) * DeliveryType.BIKE.getTimeKilometer()
                                    / DeliveryType.CAR.getTimeKilometer() - kmRestaurantClient));
                    kmWhenCandidateWins = km;
                }
                if (current.type() == MapEntityType.DELIVERY_GUY_CAR && isCarInPriceLimit &&
                        (isCarInPriceLimit = inPriceLimit(km, kmRestaurantClient, DeliveryType.CAR, maxPrice))) {
                    return buildDeliveryInfo(current, kmRestaurantClient, km);
                }
                addNeighbours(location, wentTrough, queue);
            } size = queue.size();
            km++;
        }
        return buildDeliveryInfoForPrice(candidate, kmRestaurantClient, kmWhenCandidateWins, maxPrice);
    }
}
