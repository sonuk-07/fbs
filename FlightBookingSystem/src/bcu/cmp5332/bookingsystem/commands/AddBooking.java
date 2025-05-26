package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;

public class AddBooking implements Command {
    private final int customerId;
    private final int flightId;
    private final CommercialClassType selectedClass;

    // Constructor with class selection
    public AddBooking(int customerId, int flightId, CommercialClassType selectedClass) {
        this.customerId = customerId;
        this.flightId = flightId;
        this.selectedClass = selectedClass;
    }

    // Constructor without class selection (defaults to economy)
    public AddBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
        this.selectedClass = CommercialClassType.ECONOMY;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        
        if (customer == null || flight == null) {
            throw new FlightBookingSystemException("Invalid customer or flight ID");
        }

        // Check if selected class is available for this flight
        if (!flight.isClassAvailable(selectedClass)) {
            throw new FlightBookingSystemException("Selected class " + selectedClass.getClassName() + 
                " is not available for this flight");
        }

        // Add booking with class information (you'll need to update FlightBookingSystem.addBooking method)
        flightBookingSystem.addBooking(customer, flight);
        
        System.out.println("Booking added for Customer #" + customerId + 
                          " to Flight #" + flightId + 
                          " in " + selectedClass.getClassName() + 
                          " class for £" + flight.getPriceForClass(selectedClass));
    }
}