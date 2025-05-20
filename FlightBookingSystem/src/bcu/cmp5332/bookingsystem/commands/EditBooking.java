package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EditBooking implements Command {
    private final int bookingId;
    private final int flightId;

    public EditBooking(int bookingId, int flightId) {
        this.bookingId = bookingId;
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Booking bookingToEdit = null;

        for (Customer customer : fbs.getCustomers()) {
            for (Booking booking : customer.getBookings()) {
                if (booking.getId() == bookingId && booking.getFlight().getId() == flightId) {
                    bookingToEdit = booking;
                    break;
                }
            }
            if (bookingToEdit != null) break;
        }

        if (bookingToEdit == null) {
            throw new FlightBookingSystemException("Booking with ID " + bookingId + " and flight ID " + flightId + " not found.");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            LocalDate newBookingDate = null;
            int attempts = 3;

            while (attempts > 0) {
                System.out.print("Enter new booking date (YYYY-MM-DD): ");
                String input = br.readLine();
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
            System.out.println("Booking updated successfully.");
        } catch (IOException e) {
            throw new FlightBookingSystemException("Error reading input.", e);
        }
    }
}
