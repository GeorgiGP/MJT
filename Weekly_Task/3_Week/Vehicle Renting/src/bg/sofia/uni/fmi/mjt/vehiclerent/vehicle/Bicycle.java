package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.Time;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Bicycle extends Vehicle {
    private double pricePerDay;
    private double pricePerHour;

    public Bicycle(String id, String model, double pricePerDay, double pricePerHour){
        super(id, model);
        this.pricePerDay = pricePerDay;
        this.pricePerHour = pricePerHour;
    }

    @Override
    public double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException {
        if (startOfRent == null || endOfRent == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (!startOfRent.isBefore(endOfRent)) {
            throw new InvalidRentingPeriodException("Renting date should be before end date");
        }
        if (!endOfRent.minusWeeks(1).isBefore(startOfRent)) {
            throw new InvalidRentingPeriodException("Renting period should be below 1 week");
        }
        long countDays = startOfRent.until(endOfRent, ChronoUnit.DAYS);
        long countHours = startOfRent.until(endOfRent, ChronoUnit.HOURS) % Time.HOURS_PER_DAY;

        return pricePerDay * countDays + pricePerHour * countHours;
    }
}
