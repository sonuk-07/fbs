package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays; // Import Arrays for easy conversion of enum values to list

public class EditBooking implements Command {
    private final int customerId; // We need customer ID to find the booking
    private final int flightId;

    public EditBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        // Retrieve the customer and flight first
        Customer customer = fbs.getCustomerByID(customerId);
        if (customer == null) {
            throw new FlightBookingSystemException("Customer with ID " + customerId + " not found.");
        }

        Flight flight = fbs.getFlightByID(flightId);
        if (flight == null) {
            throw new FlightBookingSystemException("Flight with ID " + flightId + " not found.");
        }

        Booking bookingToEdit = null;
        for (Booking booking : customer.getBookings()) {
            // Assuming a booking is uniquely identified by customer and flight for simplicity,
            // or if there's a unique booking ID within the customer's bookings.
            // If booking has its own ID, you would iterate through fbs.getBookings()
            // and match by booking.getId()
            if (booking.getFlight().getId() == flightId) { // Check if booking is for this flight
                bookingToEdit = booking;
                break;
            }
        }

        if (bookingToEdit == null) {
            throw new FlightBookingSystemException("Booking for Customer ID " + customerId + " on Flight ID " + flightId + " not found.");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            // Edit Booking Date
            LocalDate newBookingDate = null;
            int attempts = 3;
            while (attempts > 0) {
                System.out.print("Enter new booking date (YYYY-MM-DD) or press Enter to keep current (" + bookingToEdit.getBookingDate() + "): ");
                String input = br.readLine();
                if (input.isEmpty()) {
                    newBookingDate = bookingToEdit.getBookingDate(); // Keep current date
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
                throw new FlightBookingSystemException("Failed to parse valid date after multiple attempts.");
            }
            bookingToEdit.setBookingDate(newBookingDate);

            // Edit Booking Class
            CommercialClassType newClass = null;
            System.out.println("\nAvailable Classes for Flight #" + flightId + ":");
            List<CommercialClassType> availableClasses = flight.getAvailableClasses();
            for (int i = 0; i < availableClasses.size(); i++) {
                CommercialClassType classType = availableClasses.get(i);
                System.out.println((i + 1) + ". " + classType.getClassName() + " (£" + flight.getPriceForClass(classType) + ")");
            }

            attempts = 3;
            while (attempts > 0) {
                System.out.print("Enter new class number (1-" + availableClasses.size() + ") or press Enter to keep current (" + bookingToEdit.getBookedClass().getClassName() + "): ");
                String classInput = br.readLine();
                if (classInput.isEmpty()) {
                    newClass = bookingToEdit.getBookedClass(); // Keep current class
                    break;
                }
                try {
                    int classChoice = Integer.parseInt(classInput);
                    if (classChoice > 0 && classChoice <= availableClasses.size()) {
                        newClass = availableClasses.get(classChoice - 1);
                        if (flight.isClassAvailable(newClass)) {
                            bookingToEdit.setBookedClass(newClass);
                            break;
                        } else {
                            System.out.println("Selected class is not truly available (e.g., full). Please choose another. Attempts left: " + attempts);
                        }
                    } else {
                        System.out.println("Invalid class number. Please try again. Attempts left: " + attempts);
                    }
                } catch (NumberFormatException e) {
                    attempts--;
                    System.out.println("Invalid input. Please enter a number. Attempts left: " + attempts);
                } catch (FlightBookingSystemException e) {
                    // This catches the exception from getPriceForClass if the class is not available
                    System.out.println(e.getMessage() + " Please choose another. Attempts left: " + attempts);
                    attempts--;
                }
            }

            if (newClass == null) {
                throw new FlightBookingSystemException("Failed to select a valid class after multiple attempts.");
            }
            bookingToEdit.setBookedClass(newClass);

            System.out.println("Booking updated successfully.");
            System.out.println(bookingToEdit.getDetailsLong());

        } catch (IOException e) {
            throw new FlightBookingSystemException("Error reading input.", e);
        }
    }
}