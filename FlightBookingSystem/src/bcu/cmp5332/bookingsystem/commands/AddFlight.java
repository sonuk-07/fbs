package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;

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
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        int maxId = 0;
        for (Flight f : flightBookingSystem.getFlights()) {
            if (f.getId() > maxId) {
                maxId = f.getId();
            }
        }

        Flight flight;
        if (flightType == FlightType.BUDGET) {
            flight = new Flight(++maxId, flightNumber, origin, destination, 
                              departureDate, economyPrice, capacity);
        } else {
            flight = new Flight(++maxId, flightNumber, origin, destination, 
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
    }
}