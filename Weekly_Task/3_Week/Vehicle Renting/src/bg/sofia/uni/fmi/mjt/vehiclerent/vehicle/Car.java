package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.Time;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Car extends Vehicle {
    private FuelType fuelType;

    private int numberOfSeats;
    private final static double SEATS_TAX = 5;

    private double pricePerWeek;
    private double pricePerDay;
    private double pricePerHour;


    public Car(String id, String model, FuelType fuelType, int numberOfSeats, double pricePerWeek, double pricePerDay, double pricePerHour) {
        super(id, model);
        this.fuelType = fuelType;
        this.numberOfSeats = numberOfSeats;
        this.pricePerWeek = pricePerWeek;
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
        if (!endOfRent.minusMonths(1).isBefore(startOfRent)) {
            throw new InvalidRentingPeriodException("Renting period should be below 1 month");
        }
        long countWeeks = startOfRent.until(endOfRent, ChronoUnit.WEEKS);
        long countDays = startOfRent.until(endOfRent, ChronoUnit.DAYS) % Time.DAYS_PER_WEEK;
        long countHours = startOfRent.until(endOfRent, ChronoUnit.HOURS) % Time.HOURS_PER_DAY;


        long allDays = countWeeks * Time.DAYS_PER_WEEK + countDays;
        double fuelTax = fuelType.getTax() * allDays;
        double seatsTax = numberOfSeats * SEATS_TAX;
        double durationTax = pricePerDay * countDays + pricePerHour * countHours + pricePerWeek * countWeeks;
        return durationTax + driver.getTax() + fuelTax + seatsTax;
    }


}
