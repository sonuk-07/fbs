package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EditBooking implements Command {
    private final int customerId;
    private final int flightId;

    public EditBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem fbs, BufferedReader reader) throws FlightBookingSystemException {
        Customer customer = fbs.getCustomerByID(customerId);
        if (customer == null) {
            throw new FlightBookingSystemException("Customer with ID " + customerId + " not found.");
        }

        // Get flight
        Flight flight = fbs.getFlightByID(flightId);
        if (flight == null) {
            throw new FlightBookingSystemException("Flight with ID " + flightId + " not found.");
        }

        // Find booking
        Booking bookingToEdit = null;
        for (Booking booking : customer.getBookings()) {
            if ((booking.getOutboundFlight() != null && booking.getOutboundFlight().getId() == flightId) ||
                (booking.getReturnFlight() != null && booking.getReturnFlight().getId() == flightId)) {
                bookingToEdit = booking;
                break;
            }
        }     

        if (bookingToEdit == null) {
            throw new FlightBookingSystemException("Booking for Customer ID " + customerId + " on Flight ID " + flightId + " not found.");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            // --- Edit Booking Date ---
            LocalDate newBookingDate = null;
            int attempts = 3;
            while (attempts > 0) {
                System.out.print("Enter new booking date (YYYY-MM-DD) or press Enter to keep current (" + bookingToEdit.getBookingDate() + "): ");
                String input = br.readLine();
                if (input.isEmpty()) {
                    newBookingDate = bookingToEdit.getBookingDate();
                    break;
                }
                try {
                    newBookingDate = LocalDate.parse(input);
                    break;
                } catch (DateTimeParseException e) {
                    attempts--;
                    System.out.println("Invalid date format. Please try again. Attempts left: " + attempts);
                }
            }

            if (newBookingDate == null) {
                throw new FlightBookingSystemException("Failed to parse a valid date after multiple attempts.");
            }
            bookingToEdit.setBookingDate(newBookingDate);

            // --- Edit Booking Class ---
            CommercialClassType newClass = null;
            List<CommercialClassType> availableClasses = flight.getAvailableClasses();

            System.out.println("\nAvailable Classes for Flight #" + flightId + ":");
            for (int i = 0; i < availableClasses.size(); i++) {
                CommercialClassType classType = availableClasses.get(i);
                System.out.println((i + 1) + ". " + classType.getClassName() + " (£" + flight.getPriceForClass(classType) + ")");
            }

            attempts = 3;
            while (attempts > 0) {
                System.out.print("Enter new class number (1-" + availableClasses.size() + ") or press Enter to keep current (" + bookingToEdit.getBookedClass().getClassName() + "): ");
                String classInput = br.readLine();
                if (classInput.isEmpty()) {
                    newClass = bookingToEdit.getBookedClass();
                    break;
                }
                try {
                    int classChoice = Integer.parseInt(classInput);
                    if (classChoice > 0 && classChoice <= availableClasses.size()) {
                        CommercialClassType selectedClass = availableClasses.get(classChoice - 1);
                        if (flight.isClassAvailable(selectedClass)) {
                            newClass = selectedClass;
                            break;
                        } else {
                            System.out.println("Selected class is not available. Attempts left: " + --attempts);
                        }
                    } else {
                        System.out.println("Invalid class number. Please try again. Attempts left: " + --attempts);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number. Attempts left: " + --attempts);
                }
            }

            if (newClass == null) {
                throw new FlightBookingSystemException("Failed to select a valid class after multiple attempts.");
            }

            bookingToEdit.setBookedClass(newClass);

            System.out.println("\nBooking updated successfully.");
            System.out.println(bookingToEdit.getDetailsLong());

        } catch (IOException e) {
            throw new FlightBookingSystemException("Error reading input.", e);
        }
    }
}
