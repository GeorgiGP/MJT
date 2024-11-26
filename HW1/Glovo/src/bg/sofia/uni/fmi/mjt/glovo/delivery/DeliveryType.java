package bg.sofia.uni.fmi.mjt.glovo.delivery;

public enum DeliveryType {
    CAR(5, 3),
    BIKE(3, 5);

    private final int priceKilometer;
    private final int timeKilometer;

    DeliveryType(int priceKilometer, int timeKilometer) {
        this.priceKilometer = priceKilometer;
        this.timeKilometer = timeKilometer;
    }

    public int getPriceKilometer() {
        return priceKilometer;
    }

    public int getTimeKilometer() {
        return timeKilometer;
    }
}
