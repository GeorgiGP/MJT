package bg.sofia.uni.fmi.mjt.vehiclerent.driver;

public record Driver(AgeGroup ageGroup) {
    public int getTax() {
        return ageGroup.getTax();
    }
}
