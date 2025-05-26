package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.*;
import bcu.cmp5332.bookingsystem.model.CommercialClassType; // Import CommercialClassType
import bcu.cmp5332.bookingsystem.model.FlightType; // Import FlightType

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class CommandParser {

    public static Command parse(String line) throws IOException, FlightBookingSystemException {
        try {
            String[] parts = line.trim().split(" ", 3);
            String cmd = parts[0].toLowerCase(); // case-insensitive

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

                    System.out.print("Economy Price: "); // Changed prompt
                    String economyPriceStr = reader.readLine();
                    BigDecimal economyPrice;
                    try {
                        economyPrice = new BigDecimal(economyPriceStr);
                    } catch (NumberFormatException e) {
                        throw new FlightBookingSystemException("Invalid price format. Please enter a number.");
                    }

                    System.out.print("Total Capacity: "); // Changed prompt
                    int capacity;
                    try {
                        capacity = Integer.parseInt(reader.readLine());
                        if (capacity <= 0) {
                            throw new FlightBookingSystemException("Capacity must be a positive integer.");
                        }
                    } catch (NumberFormatException e) {
                        throw new FlightBookingSystemException("Invalid capacity format. Please enter an integer.");
                    }

                    System.out.print("Flight Type (BUDGET/COMMERCIAL): ");
                    String flightTypeInput = reader.readLine().toUpperCase();
                    FlightType flightType;
                    try {
                        flightType = FlightType.valueOf(flightTypeInput);
                    } catch (IllegalArgumentException e) {
                        throw new FlightBookingSystemException("Invalid flight type. Must be BUDGET or COMMERCIAL.");
                    }

                    if (flightType == FlightType.BUDGET) {
                        return new AddFlight(flightNumber, origin, destination, departureDate, economyPrice, capacity);
                    } else {
                        // For COMMERCIAL flights, ask if user wants custom class capacities or default
                        System.out.print("Use custom class capacities? (yes/no - default is no): ");
                        String customCapacityChoice = reader.readLine().trim().toLowerCase();
                        if ("yes".equals(customCapacityChoice)) {
                            Map<CommercialClassType, Integer> customCapacities = new HashMap<>();
                            int remainingCapacity = capacity;
                            System.out.println("Enter capacities for each class (remaining total capacity: " + remainingCapacity + "):");
                            for (CommercialClassType classType : CommercialClassType.values()) {
                                System.out.print("Capacity for " + classType.getClassName() + " (current remaining: " + remainingCapacity + "): ");
                                try {
                                    int classCapacity = Integer.parseInt(reader.readLine());
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
                                System.err.println("Warning: Total specified class capacities (" + (capacity - remainingCapacity) + ") does not match total flight capacity (" + capacity + "). Using provided values.");
                            }
                            return new AddFlight(flightNumber, origin, destination, departureDate, economyPrice, capacity, customCapacities);
                        } else {
                            // Use the constructor for Commercial flights with default distribution
                            return new AddFlight(flightNumber, origin, destination, departureDate, economyPrice, capacity, flightType);
                        }
                    }

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

                case "addbooking": // Enhanced addbooking
                    if (parts.length < 3) {
                        throw new FlightBookingSystemException("Usage: addbooking <customer ID> <flight ID>");
                    }
                    int customerId = Integer.parseInt(parts[1]);
                    int flightId = Integer.parseInt(parts[2]);

                    // Additional input for booking class
                    System.out.print("Enter desired booking class (ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST - default ECONOMY): ");
                    String classInput = reader.readLine().trim().toUpperCase();
                    CommercialClassType selectedClass = CommercialClassType.ECONOMY; // Default

                    if (!classInput.isEmpty()) {
                        try {
                            selectedClass = CommercialClassType.valueOf(classInput);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid class entered. Defaulting to ECONOMY.");
                        }
                    }
                    return new AddBooking(customerId, flightId, selectedClass);

                case "editbooking": // Enhanced editbooking
                    if (parts.length < 3) {
                        throw new FlightBookingSystemException("Usage: editbooking <customer ID> <flight ID>");
                    }
                    int editCustomerId = Integer.parseInt(parts[1]);
                    int editFlightId = Integer.parseInt(parts[2]);
                    return new EditBooking(editCustomerId, editFlightId);


                case "showflight":
                    if (parts.length < 2) throw new FlightBookingSystemException("Usage: showflight <flight ID>");
                    return new ShowFlight(Integer.parseInt(parts[1]));

                case "showcustomer":
                    if (parts.length < 2) throw new FlightBookingSystemException("Usage: showcustomer <customer ID>");
                    return new ShowCustomer(Integer.parseInt(parts[1]));

                case "cancelbooking":
                    if (parts.length < 3) {
                        throw new FlightBookingSystemException("Usage: cancelbooking <customer ID> <flight ID>");
                    }
                    int cancelCustomerId = Integer.parseInt(parts[1]);
                    int cancelFlightId = Integer.parseInt(parts[2]);
                    return new CancelBooking(cancelCustomerId, cancelFlightId);


                default:
                    throw new FlightBookingSystemException("Unknown command.");
            }
        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Invalid number format in command.");
        }
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