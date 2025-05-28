package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
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
        this.classCapacities = null; // Will be set automatically for budget
    }

    // Constructor for Commercial flights with custom class capacities
    public AddFlight(String flightNumber, String origin, String destination,
                     LocalDate departureDate, BigDecimal economyPrice, int capacity,
                     Map<CommercialClassType, Integer> classCapacities) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.economyPrice = economyPrice;
        this.capacity = capacity;
        this.flightType = FlightType.COMMERCIAL;
        this.classCapacities = classCapacities;
    }

    // Constructor for Commercial flights with default class distribution
    public AddFlight(String flightNumber, String origin, String destination,
                     LocalDate departureDate, BigDecimal economyPrice, int capacity,
                     FlightType flightType) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.economyPrice = economyPrice;
        this.capacity = capacity;
        this.flightType = flightType;
        this.classCapacities = null; // Will use default distribution
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        int nextId = flightBookingSystem.generateNextFlightId();
        // --- CHANGE END ---

        Flight flight;
        if (flightType == FlightType.BUDGET) {
            flight = new Flight(nextId, flightNumber, origin, destination, 
                                departureDate, economyPrice, capacity);
        } else {
            flight = new Flight(nextId, flightNumber, origin, destination, 
                                departureDate, economyPrice, capacity, flightType, classCapacities);
        }

        flightBookingSystem.addFlight(flight);
        
        System.out.println("Flight #" + flight.getId() + " added.");
        System.out.println("Flight Type: " + flightType);
        if (flightType == FlightType.COMMERCIAL) {
            System.out.println("Available Classes and Prices:");
            // For displaying actual prices after addition, ensure getPriceForClass considers systemDate if dynamic
            // or just reflects the base price if not yet dynamic.
            for (CommercialClassType classType : flight.getAvailableClasses()) {
                // If you want to show dynamic price here, you'd call:
                // System.out.println("  " + classType.getClassName() + ": £" + flight.getDynamicPrice(classType, flightBookingSystem.getSystemDate()));
                // Otherwise, the base price is fine for "Available Classes and Prices:"
                System.out.println("  " + classType.getClassName() + ": £" + flight.getPriceForClass(classType)); 
            }
        } else {
            System.out.println("Single Class Price: £" + economyPrice);
        }
    }
}
