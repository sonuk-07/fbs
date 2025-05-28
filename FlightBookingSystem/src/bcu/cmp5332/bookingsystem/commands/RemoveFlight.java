package bcu.cmp5332.bookingsystem.commands;

import java.io.BufferedReader;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class RemoveFlight implements Command {
    private final int flightId;

    public RemoveFlight(int flightId) {
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem fbs, BufferedReader reader) throws FlightBookingSystemException {
        Flight flight = fbs.getFlightByID(flightId);
        if (flight == null || flight.isDeleted()) {
            throw new FlightBookingSystemException("Flight with ID " + flightId + " does not exist or is already removed.");
        }

        flight.setDeleted(true);

        System.out.println("Flight " + flight.getFlightNumber() + " (ID: " + flightId + ") has been removed.");
    }
}
