package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;
import java.util.List;

public class ListAllFlights implements Command {

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        List<Flight> allFlights = flightBookingSystem.getAllFlights(); // Use the new method
        if (allFlights.isEmpty()) {
            System.out.println("No flights in the system (including deleted ones).");
            return;
        }

        System.out.println("All Flights (including deleted):");
        for (Flight flight : allFlights) {
            String status = flight.isDeleted() ? " (DELETED)" : "";
            System.out.println(flight.getDetailsShort() + status);
        }
        System.out.println(allFlights.size() + " flight(s)");
    }
}