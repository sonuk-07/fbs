package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CommandParser {

    public static Command parse(String line) throws IOException, FlightBookingSystemException {
        try {
            String[] parts = line.trim().split(" ", 3);
            String cmd = parts[0].toLowerCase();  // case-insensitive

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            switch (cmd) {

                case "addflight":
                    System.out.print("Flight Number: ");
                    String flightNumber = reader.readLine();
                    System.out.print("Origin: ");
                    String origin = reader.readLine();
                    System.out.print("Destination: ");
                    String destination = reader.readLine();
                    LocalDate departureDate = parseDateWithAttempts(reader);
                    return new AddFlight(flightNumber, origin, destination, departureDate);

                case "addcustomer":
                    System.out.print("Customer Name: ");
                    String name = reader.readLine();
                    System.out.print("Customer Phone: ");
                    String phone = reader.readLine();
                    return new AddCustomer(name, phone);

                case "listflights":
                    return new ListFlights();

                case "listcustomers":
                    return new ListCustomers();

                case "loadgui":
                    return new LoadGUI();

                case "help":
                    return new Help();
                case "exit":
                	return new Exit();

                default:
                    if (parts.length == 2) {
                         int id = Integer.parseInt(parts[1]);
                         if (cmd.equals("showflight")) {
                             return new ShowFlight(id);
                         } else if (cmd.equals("showcustomer")) {
                             return new ShowCustomer(id);
                         }
                    } else if (parts.length == 3) {
                        int id1 = Integer.parseInt(parts[1]);
                        int id2 = Integer.parseInt(parts[2]);

                        if (cmd.equals("addbooking")) {
                            return new AddBooking(id1, id2);   // id1 = customerId, id2 = flightId
                        } else if (cmd.equals("editbooking")) {
                            return new EditBooking(id1, id2);  // id1 = bookingId, id2 = flightId (based on your description)
                        } else if (cmd.equals("cancelbooking")) {
                            return new CancelBooking(id1, id2);
                        }
                    }

                    break;
            }

        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Invalid number format in command.");
        }

        throw new FlightBookingSystemException("Invalid command.");
    }

    private static LocalDate parseDateWithAttempts(BufferedReader br, int attempts)
            throws IOException, FlightBookingSystemException {
        if (attempts < 1) {
            throw new IllegalArgumentException("Number of attempts should be more than 0");
        }
        while (attempts > 0) {
            System.out.print("Departure Date (YYYY-MM-DD): ");
            String input = br.readLine();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                attempts--;
                System.out.println("Invalid format. Please use YYYY-MM-DD. Attempts remaining: " + attempts);
            }
        }
        throw new FlightBookingSystemException("Failed to parse valid date after multiple attempts.");
    }

    private static LocalDate parseDateWithAttempts(BufferedReader br)
            throws IOException, FlightBookingSystemException {
        return parseDateWithAttempts(br, 3);
    }
}
