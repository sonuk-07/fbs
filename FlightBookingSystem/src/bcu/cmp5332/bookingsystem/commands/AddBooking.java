package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class AddBooking implements Command {

    private final int customerId;
    private final int flightId;

    public AddBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
    	Customer customer = flightBookingSystem.getCustomerByID(customerId);
    	Flight flight  = flightBookingSystem.getFlightByID(flightId);
    	if(customer== null || flight==null) {
    		throw new FlightBookingSystemException("Invalid customer or flight Id");
    	}
    	flightBookingSystem.addBooking(customer,flight);
        System.out.println("Booking added for Customer #" + customerId + " to Flight #" + flightId);
    	
    }
}
