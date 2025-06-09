// bcu.cmp5332.bookingsystem.commands.AddFlight.java
package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    public AddFlight(String flightNumber, String origin, String destination,
                     LocalDate departureDate, BigDecimal economyPrice, int capacity,
                     FlightType flightType) {
        this(flightNumber, origin, destination, departureDate, economyPrice, capacity, flightType, null);
    }


    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        int nextId = flightBookingSystem.generateNextFlightId();

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
        System.out.println("Flight Number: " + flight.getFlightNumber());
        System.out.println("Origin: " + flight.getOrigin());
        System.out.println("Destination: " + flight.getDestination());
        System.out.println("Departure Date: " + flight.getDepartureDate());
        System.out.println("Total Capacity: " + flight.getCapacity());
        System.out.println("Flight Type: " + flight.getFlightType());

        if (flightType == FlightType.COMMERCIAL) {
            System.out.println("Available Classes and Capacities:");
            for (Map.Entry<CommercialClassType, Integer> entry : flight.getClassCapacities().entrySet()) {
                System.out.println("  " + entry.getKey().getClassName() + " Capacity: " + entry.getValue());
            }
            System.out.println("Available Classes and Prices:");
            for (CommercialClassType classType : flight.getAvailableClasses()) {
                 System.out.println("  " + classType.getClassName() + ": £" +
                                    flight.getPriceForClass(classType)); // Assuming this method exists in Flight
            }
        } else { 
            System.out.println("Economy Price: £" + economyPrice);
        }
    }
}