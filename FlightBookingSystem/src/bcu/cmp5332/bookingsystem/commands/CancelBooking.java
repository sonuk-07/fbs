package bcu.cmp5332.bookingsystem.commands;

import java.io.BufferedReader;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Booking; // Import Booking class

public class CancelBooking implements Command {

    private final int customerId;
    private final int flightId; // This flightId could be either outbound or return flight of a booking

    public CancelBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight flight = flightBookingSystem.getFlightByIDIncludingDeleted(flightId); // Use getFlightByIDIncludingDeleted to find the flight even if departed/deleted

        if (flight == null) {
            throw new FlightBookingSystemException("Flight not found with ID: " + flightId);
        }
        
        // The core logic for finding the booking, applying the fee, and removing passengers
        // is now in FlightBookingSystem.cancelBooking.
        flightBookingSystem.cancelBooking(customer, flight);
        
        // Success message is printed by FlightBookingSystem.cancelBooking now.
    }
}
