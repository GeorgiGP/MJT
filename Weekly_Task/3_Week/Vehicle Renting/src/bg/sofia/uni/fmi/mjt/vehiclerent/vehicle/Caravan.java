package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;

import java.time.LocalDateTime;

public class Caravan extends Car {

    private int numberOfBeds;
    private final static double BEDS_TAX = 10;

    public Caravan(String id, String model, FuelType fuelType, int numberOfSeats, int numberOfBeds, double pricePerWeek, double pricePerDay, double pricePerHour) {
        super(id, model, fuelType, numberOfSeats, pricePerWeek, pricePerDay, pricePerHour);
        this.numberOfBeds = numberOfBeds;
    }

    @Override
    public double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException {
        if (endOfRent.minusDays(1).isBefore(startOfRent)) {
            throw new InvalidRentingPeriodException("Renting period should be more than 24 hours");
        }
        double bedsTax = numberOfBeds * BEDS_TAX;
        return super.calculateRentalPrice(startOfRent, endOfRent) + bedsTax;
    }

}
