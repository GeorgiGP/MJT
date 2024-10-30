package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

public enum FuelType {
    DIESEL(3),
    PETROL(3),
    HYBRID(1),
    ELECTRICITY(0),
    HYDROGEN(0);

    private int tax;

    FuelType(int tax) {
        this.tax = tax;
    }

    public int getTax() {
        return tax;
    }


}
