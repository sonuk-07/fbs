package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal; // Import BigDecimal

public class AddBooking implements Command {
    private final int customerId;
    private final int outboundFlightId;
    private final Integer returnFlightId; // nullable
    private final CommercialClassType selectedClass;

    // Constructor for single-leg booking
    public AddBooking(int customerId, int outboundFlightId, CommercialClassType selectedClass) {
        this(customerId, outboundFlightId, null, selectedClass);
    }

    // Constructor for round-trip booking
    public AddBooking(int customerId, int outboundFlightId, Integer returnFlightId, CommercialClassType selectedClass) {
        this.customerId = customerId;
        this.outboundFlightId = outboundFlightId;
        this.returnFlightId = returnFlightId;
        this.selectedClass = selectedClass;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        try {
            Customer customer = flightBookingSystem.getCustomerByID(customerId);
            Flight outbound = flightBookingSystem.getFlightByID(outboundFlightId); // This already checks for active/future
            Flight returnFlight = null;
            if (returnFlightId != null) {
                returnFlight = flightBookingSystem.getFlightByID(returnFlightId); // This also checks for active/future
            }

            // --- Pre-booking Checks and Price Display ---

            // Check outbound flight capacity and class availability
            if (!outbound.isClassAvailable(selectedClass)) {
                throw new FlightBookingSystemException("Selected class is not available on outbound flight " + outbound.getFlightNumber() + " (ID: " + outbound.getId() + ").");
            }
            if (!outbound.hasAnySeatsLeft()) {
                throw new FlightBookingSystemException("Outbound flight " + outbound.getFlightNumber() + " (ID: " + outbound.getId() + ") is at full capacity.");
            }

            BigDecimal outboundPrice = outbound.getDynamicPrice(selectedClass, flightBookingSystem.getSystemDate());
            System.out.println("\n--- Booking Details ---");
            System.out.println("Customer: " + customer.getName() + " (ID: " + customer.getId() + ")");
            System.out.println("Outbound Flight: " + outbound.getFlightNumber() + " (ID: " + outbound.getId() + ") - " + outbound.getOrigin() + " to " + outbound.getDestination());
            System.out.println("  Class: " + selectedClass.getClassName());
            System.out.println("  Price: £" + outboundPrice);

            BigDecimal returnPrice = BigDecimal.ZERO;
            if (returnFlight != null) {
                // Check return flight capacity and class availability
                if (!returnFlight.isClassAvailable(selectedClass)) {
                    throw new FlightBookingSystemException("Selected class is not available on return flight " + returnFlight.getFlightNumber() + " (ID: " + returnFlight.getId() + ").");
                }
                if (!returnFlight.hasAnySeatsLeft()) {
                    throw new FlightBookingSystemException("Return flight " + returnFlight.getFlightNumber() + " (ID: " + returnFlight.getId() + ") is at full capacity.");
                }

                returnPrice = returnFlight.getDynamicPrice(selectedClass, flightBookingSystem.getSystemDate());
                System.out.println("Return Flight: " + returnFlight.getFlightNumber() + " (ID: " + returnFlight.getId() + ") - " + returnFlight.getOrigin() + " to " + returnFlight.getDestination());
                System.out.println("  Class: " + selectedClass.getClassName());
                System.out.println("  Price: £" + returnPrice);
            }

            BigDecimal totalBookingPrice = outboundPrice.add(returnPrice);
            System.out.println("Total Estimated Booking Price: £" + totalBookingPrice.setScale(2, BigDecimal.ROUND_HALF_UP));

            // --- Confirmation ---
            System.out.print("Confirm booking? (yes/no): ");
            String confirmation = reader.readLine().trim().toLowerCase();

            if (!"yes".equals(confirmation)) {
                System.out.println("Booking cancelled by user.");
                return; // Exit without making the booking
            }

            flightBookingSystem.addBooking(customer, outbound, returnFlight, selectedClass);

            System.out.println("Booking completed for Customer #" + customerId + " in " + selectedClass.getClassName() +
                               " on Flight " + outbound.getFlightNumber() +
                               (returnFlight != null ? " and Return Flight " + returnFlight.getFlightNumber() : "") + ".");

        } catch (IOException e) {
            throw new FlightBookingSystemException("Error reading input during booking confirmation: " + e.getMessage());
        }
    }
}
