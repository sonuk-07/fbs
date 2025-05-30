package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
// Removed: import bcu.cmp5332.bookingsystem.model.Meal; // No longer needed here

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFlight implements Command {

    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDate departureDate;
    private final BigDecimal economyPrice;
    private final int capacity;
    private final FlightType flightType;
    private final Map<CommercialClassType, Integer> classCapacities;

    // Constructor for Budget flights
    public AddFlight(String flightNumber, String origin, String destination,
                     LocalDate departureDate, BigDecimal economyPrice, int capacity) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.economyPrice = economyPrice;
        this.capacity = capacity;
        this.flightType = FlightType.BUDGET;
        this.classCapacities = null;
    }

    // Constructor for Commercial flights with custom class capacities
    public AddFlight(String flightNumber, String origin, String destination,
                     LocalDate departureDate, BigDecimal economyPrice, int capacity,
                     FlightType flightType, Map<CommercialClassType, Integer> classCapacities) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.economyPrice = economyPrice;
        this.capacity = capacity;
        this.flightType = flightType;
        this.classCapacities = classCapacities;
    }

    // Constructor for Commercial flights with default class distribution
    public AddFlight(String flightNumber, String origin, String destination,
                     LocalDate departureDate, BigDecimal economyPrice, int capacity,
                     FlightType flightType) {
        this(flightNumber, origin, destination, departureDate, economyPrice, capacity, flightType, null);
    }


    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        int nextId = flightBookingSystem.generateNextFlightId();

        // Removed all meal quantity assignment logic for flights
        // Map<Integer, Integer> flightMealQuantities = new HashMap<>();
        // List<Meal> activeMeals = flightBookingSystem.getMeals();
        // ... (removed meal assignment prompts) ...

        Flight flight;
        if (flightType == FlightType.BUDGET) {
            // Flight constructor no longer takes mealQuantities
            flight = new Flight(nextId, flightNumber, origin, destination,
                                departureDate, economyPrice, capacity);
        } else { // Commercial
            // Flight constructor no longer takes mealQuantities
            flight = new Flight(nextId, flightNumber, origin, destination,
                                departureDate, economyPrice, capacity, flightType, classCapacities);
        }

        flightBookingSystem.addFlight(flight);

        System.out.println("Flight #" + flight.getId() + " added.");
        System.out.println("Flight Type: " + flightType);
        if (flightType == FlightType.COMMERCIAL) {
            System.out.println("Available Classes and Prices:");
            for (CommercialClassType classType : flight.getAvailableClasses()) {
                System.out.println("  " + classType.getClassName() + ": £" +
                                   flight.getPriceForClass(classType));
            }
        } else {
            System.out.println("Single Class Price: £" + economyPrice);
        }
        // Removed meal display logic from here as well
        // if (!flightMealQuantities.isEmpty()) { ... }
    }
}
