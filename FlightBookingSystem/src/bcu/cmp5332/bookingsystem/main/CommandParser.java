package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.*;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;
import java.io.IOException;
// REMOVE THIS IF YOU HAD IT: import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class CommandParser {

    // --- CRITICAL CHANGE HERE ---
    // The parse method must accept the BufferedReader passed from Main.java
    public static Command parse(String line, FlightBookingSystem fbs, BufferedReader reader) throws IOException, FlightBookingSystemException {
        try {
            String[] parts = line.trim().split(" ", 3);
            String cmd = parts[0].toLowerCase(); // case-insensitive

            // --- CRITICAL CHANGE HERE ---
            // REMOVE the creation of BufferedReader here. It should be passed in from Main.
            // BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            switch (cmd) {
                case "addflight":
                    System.out.print("Flight Number: ");
                    String flightNumber = reader.readLine(); // Use the passed reader
                    System.out.print("Origin: ");
                    String origin = reader.readLine(); // Use the passed reader
                    System.out.print("Destination: ");
                    String destination = reader.readLine(); // Use the passed reader
                    LocalDate departureDate = parseDateWithAttempts(reader); // Use the passed reader

                    System.out.print("Economy Price: ");
                    String economyPriceStr = reader.readLine(); // Use the passed reader
                    BigDecimal economyPrice;
                    try {
                        economyPrice = new BigDecimal(economyPriceStr);
                    } catch (NumberFormatException e) {
                        throw new FlightBookingSystemException("Invalid price format. Please enter a number.");
                    }

                    System.out.print("Total Capacity: ");
                    int capacity;
                    try {
                        capacity = Integer.parseInt(reader.readLine()); // Use the passed reader
                        if (capacity <= 0) {
                            throw new FlightBookingSystemException("Capacity must be a positive integer.");
                        }
                    } catch (NumberFormatException e) {
                        throw new FlightBookingSystemException("Invalid capacity format. Please enter an integer.");
                    }

                    System.out.print("Flight Type (BUDGET/COMMERCIAL): ");
                    String flightTypeInput = reader.readLine().toUpperCase(); // Use the passed reader
                    FlightType flightType;
                    try {
                        flightType = FlightType.valueOf(flightTypeInput);
                    } catch (IllegalArgumentException e) {
                        throw new FlightBookingSystemException("Invalid flight type. Must be BUDGET or COMMERCIAL.");
                    }

                    if (flightType == FlightType.BUDGET) {
                        return new AddFlight(flightNumber, origin, destination, departureDate, economyPrice, capacity);
                    } else {
                        System.out.print("Use custom class capacities? (yes/no - default is no): ");
                        String customCapacityChoice = reader.readLine().trim().toLowerCase(); // Use the passed reader
                        if ("yes".equals(customCapacityChoice)) {
                            Map<CommercialClassType, Integer> customCapacities = new HashMap<>();
                            int remainingCapacity = capacity;
                            System.out.println("Enter capacities for each class (current total capacity: " + capacity + ", remaining: " + remainingCapacity + "):");
                            for (CommercialClassType classType : CommercialClassType.values()) {
                                System.out.print("Capacity for " + classType.getClassName() + " (current remaining: " + remainingCapacity + "): ");
                                try {
                                    int classCapacity = Integer.parseInt(reader.readLine()); // Use the passed reader
                                    if (classCapacity < 0 || classCapacity > remainingCapacity) {
                                        throw new FlightBookingSystemException("Invalid capacity for " + classType.getClassName() + ". Must be between 0 and " + remainingCapacity + ".");
                                    }
                                    customCapacities.put(classType, classCapacity);
                                    remainingCapacity -= classCapacity;
                                } catch (NumberFormatException e) {
                                    throw new FlightBookingSystemException("Invalid number for capacity. Please enter an integer.");
                                }
                            }
                             if (remainingCapacity != 0) {
                                System.err.println("Warning: Sum of specified class capacities (" + (capacity - remainingCapacity) + ") does not match total flight capacity (" + capacity + "). Using provided values.");
                            }
                            return new AddFlight(flightNumber, origin, destination, departureDate, economyPrice, capacity, customCapacities);
                        } else {
                            return new AddFlight(flightNumber, origin, destination, departureDate, economyPrice, capacity, flightType);
                        }
                    }

                case "addcustomer":
                    System.out.print("Customer Name: ");
                    String name = reader.readLine(); // Use the passed reader
                    System.out.print("Customer Phone: ");
                    String phone = reader.readLine(); // Use the passed reader
                    System.out.print("Customer Email: ");
                    String email = reader.readLine(); // Use the passed reader
                    int newCustomerId = fbs.generateNextCustomerId();
                    return new AddCustomer(newCustomerId, name, phone, email);

                case "removecustomer":
                    if (parts.length < 2) {
                        throw new FlightBookingSystemException("Usage: removecustomer <customer ID>");
                    }
                    int customerIdToRemove = Integer.parseInt(parts[1]);
                    // Commands now need the reader, so pass it to them
                    return new RemoveCustomer(customerIdToRemove);

                case "listflights":
                    // Commands now need the reader, so pass it to them
                    return new ListFlights();

                case "listallflights":
                    // Commands now need the reader, so pass it to them
                    return new ListAllFlights();

                case "listcustomers":
                    // Commands now need the reader, so pass it to them
                    return new ListCustomers();

                case "listallcustomers":
                    // Commands now need the reader, so pass it to them
                    return new ListAllCustomers();

                case "removeflight":
                    if (parts.length < 2) {
                        throw new FlightBookingSystemException("Usage: removeflight <flight ID>");
                    }
                    int flightIdToRemove = Integer.parseInt(parts[1]);
                    // Commands now need the reader, so pass it to them
                    return new RemoveFlight(flightIdToRemove);

                case "loadgui":
                    return new LoadGUI();

                case "help":
                    return new Help();
                case "exit":
                    return new Exit();

                case "addbooking":
                    if (parts.length < 3) {
                        throw new FlightBookingSystemException("Usage: addbooking <customer ID> <flight ID>");
                    }
                    int customerId = Integer.parseInt(parts[1]);
                    int flightId = Integer.parseInt(parts[2]);

                    System.out.print("Enter desired booking class (ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST - default ECONOMY): ");
                    String classInput = reader.readLine().trim().toUpperCase(); // Use the passed reader
                    CommercialClassType selectedClass = CommercialClassType.ECONOMY;

                    if (!classInput.isEmpty()) {
                        try {
                            selectedClass = CommercialClassType.valueOf(classInput);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid class entered. Defaulting to ECONOMY.");
                        }
                    }
                    // Commands now need the reader, so pass it to them (no change needed here as it was already fixed)
                    return new AddBooking(customerId, flightId, selectedClass);

                case "editbooking":
                    if (parts.length < 3) {
                        throw new FlightBookingSystemException("Usage: editbooking <customer ID> <flight ID>");
                    }
                    int editCustomerId = Integer.parseInt(parts[1]);
                    int editFlightId = Integer.parseInt(parts[2]);
                    // Commands now need the reader, so pass it to them
                    return new EditBooking(editCustomerId, editFlightId);

                case "showflight":
                    if (parts.length < 2) throw new FlightBookingSystemException("Usage: showflight <flight ID>");
                    // Commands now need the reader, so pass it to them
                    return new ShowFlight(Integer.parseInt(parts[1]));

                case "showcustomer":
                    if (parts.length < 2) throw new FlightBookingSystemException("Usage: showcustomer <customer ID>");
                    // Commands now need the reader, so pass it to them
                    return new ShowCustomer(Integer.parseInt(parts[1]));

                case "cancelbooking":
                    if (parts.length < 3) {
                        throw new FlightBookingSystemException("Usage: cancelbooking <customer ID> <flight ID>");
                    }
                    int cancelCustomerId = Integer.parseInt(parts[1]);
                    int cancelFlightId = Integer.parseInt(parts[2]);
                    // Commands now need the reader, so pass it to them
                    return new CancelBooking(cancelCustomerId, cancelFlightId);

                default:
                    throw new FlightBookingSystemException("Unknown command.");
            }
        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Invalid number format in command.");
        }
    }

    // parseDateWithAttempts already accepts BufferedReader, no change needed here.
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
