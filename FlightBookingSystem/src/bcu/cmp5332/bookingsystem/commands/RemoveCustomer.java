package bcu.cmp5332.bookingsystem.commands;

import java.io.BufferedReader;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class RemoveCustomer implements Command {

    private final int customerId;

    public RemoveCustomer(int customerId) {
        this.customerId = customerId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        boolean removed = flightBookingSystem.removeCustomerById(customerId);
        if (removed) {
            System.out.println("Customer #" + customerId + " successfully removed (marked as deleted).");
        } else {
            throw new FlightBookingSystemException("Customer with ID " + customerId + " not found or already removed.");
        }
    }
}
