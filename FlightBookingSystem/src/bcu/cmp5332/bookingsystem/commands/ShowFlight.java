package bcu.cmp5332.bookingsystem.commands;

import java.io.BufferedReader;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class ShowFlight implements Command {
    private final int flightId;

    public ShowFlight(int flightId) {
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        if (flight == null) {
            throw new FlightBookingSystemException("Flight ID not found: " + flightId);
        }
        System.out.println(flight.getDetailsLong(flightBookingSystem.getSystemDate()));
    }
}
