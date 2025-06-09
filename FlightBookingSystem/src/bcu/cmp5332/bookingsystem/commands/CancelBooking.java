package bcu.cmp5332.bookingsystem.commands;

import java.io.BufferedReader;


import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to cancel an existing flight booking.
 * This command initiates the cancellation process for a specific flight
 * within a customer's booking. The actual cancellation logic, including
 * fee calculation and passenger removal, is delegated to the
 * FlightBookingSystem.
 */
public class CancelBooking implements Command {

    private final int customerId;
    private final int flightId; // This flightId could be either outbound or return flight of a booking

    
    /**
     * Constructor for the CancelBooking command.
     * @param customerId The ID of the customer whose booking is to be cancelled.
     * @param flightId The ID of the flight (either outbound or return) associated with the booking to be cancelled.
     */
    
    public CancelBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }
    
    /**
     * Executes the CancelBooking command.
     * This method retrieves the customer and the relevant flight, then calls
     * the flight booking system's `cancelBooking` method to handle the core
     * cancellation logic.
     *
     * @param flightBookingSystem The FlightBookingSystem instance.
     * @param reader The BufferedReader for user input (not directly used in this simplified cancel, but required by interface).
     * @throws FlightBookingSystemException If the customer or flight is not found, or if cancellation fails.
     */

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight flight = flightBookingSystem.getFlightByIDIncludingDeleted(flightId); // Use getFlightByIDIncludingDeleted to find the flight even if departed/deleted

        if (flight == null) {
            throw new FlightBookingSystemException("Flight not found with ID: " + flightId);
        }
        flightBookingSystem.cancelBooking(customer, flight);
    }
}
