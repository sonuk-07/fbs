package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Meal;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

public class RebookFlight implements Command {

    private final int customerId;
    private final int oldFlightId;
    private final int newFlightId;
    private final CommercialClassType newClass;

    public RebookFlight(int customerId, int oldFlightId, int newFlightId, CommercialClassType newClass) {
        this.customerId = customerId;
        this.oldFlightId = oldFlightId;
        this.newFlightId = newFlightId;
        this.newClass = newClass;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight oldFlight = flightBookingSystem.getFlightByID(oldFlightId);
        Flight newFlight = flightBookingSystem.getFlightByID(newFlightId);

        Booking bookingToRebook = null;
        for (Booking booking : customer.getBookings()) {
            if ((booking.getOutboundFlight() != null && booking.getOutboundFlight().equals(oldFlight)) ||
                (booking.getReturnFlight() != null && booking.getReturnFlight().equals(oldFlight))) {
                bookingToRebook = booking;
                break;
            }
        }

        if (bookingToRebook == null) {
            throw new FlightBookingSystemException("Customer does not have an active booking for old Flight ID: " + oldFlightId);
        }

        BigDecimal rebookFee = flightBookingSystem.calculateRebookFee(bookingToRebook, newFlight, newClass);

        if (!newFlight.isClassAvailable(newClass)) {
            throw new FlightBookingSystemException("New flight " + newFlight.getFlightNumber() + " has no seats left in " + newClass.getClassName() + " class.");
        }

        try {
            System.out.println("\n--- Rebooking Details ---");
            System.out.println("Customer: " + customer.getName());
            System.out.println("Old Flight: " + oldFlight.getFlightNumber() + " (ID: " + oldFlight.getId() + ")");
            System.out.println("New Flight: " + newFlight.getFlightNumber() + " (ID: " + newFlight.getId() + ")");
            System.out.println("New Class: " + newClass.getClassName());
            System.out.println("Rebooking Fee: £" + rebookFee);
            System.out.print("Confirm rebooking? (yes/no): ");
            String confirmation = reader.readLine().trim().toLowerCase();

            if (!"yes".equals(confirmation)) {
                System.out.println("Rebooking cancelled by user.");
                return;
            }
        } catch (IOException e) {
            throw new FlightBookingSystemException("Error reading input during rebooking confirmation: " + e.getMessage());
        }

        // Removed meal and return flight handling from here as they are not part of this simplified rebook
        // Meal oldMeal = bookingToRebook.getMeal();
        // Flight oldReturnFlight = bookingToRebook.getReturnFlight();
        // CommercialClassType oldClass = bookingToRebook.getBookedClass();

        flightBookingSystem.cancelBooking(customer, oldFlight);
        System.out.println("Old booking for Flight " + oldFlight.getFlightNumber() + " cancelled as part of rebooking.");

        try {
            // New booking for new flight. Meal is null by default for simplicity in rebook.
            flightBookingSystem.addBooking(customer, newFlight, null, newClass, null);
            System.out.println("New booking created for Flight " + newFlight.getFlightNumber() + " in " + newClass.getClassName() + " class.");
        } catch (FlightBookingSystemException e) {
            System.err.println("Error creating new booking after cancelling old one: " + e.getMessage());
            throw new FlightBookingSystemException("Rebooking failed: " + e.getMessage());
        }

        System.out.println("Flight rebooked successfully from " + oldFlight.getFlightNumber() + " to " + newFlight.getFlightNumber() + " in " + newClass.getClassName() + " class. Rebook Fee: £" + rebookFee);
    }
}
