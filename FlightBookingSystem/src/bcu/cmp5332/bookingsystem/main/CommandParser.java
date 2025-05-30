package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.*;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.MealType; // Still needed for addmeal command

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    private static final Pattern ADDBOOKING_PATTERN = Pattern.compile("^addbooking\\s+(\\d+)\\s+(\\d+)(?:\\s+(\\d+))?$");

    public static Command parse(String line, FlightBookingSystem fbs, BufferedReader reader) throws IOException, FlightBookingSystemException {
        String[] parts = line.trim().split(" ", 2); // Split into command and argument part
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "addflight":
                System.out.print("Flight Number: ");
                String flightNumber = reader.readLine();
                System.out.print("Origin: ");
                String origin = reader.readLine();
                System.out.print("Destination: ");
                String destination = reader.readLine();
                LocalDate departureDate = parseDateWithAttempts(reader);
                System.out.print("Economy Price: ");
                BigDecimal economyPrice;
                try {
                    economyPrice = new BigDecimal(reader.readLine());
                    if (economyPrice.compareTo(BigDecimal.ZERO) < 0) {
                        throw new FlightBookingSystemException("Economy price cannot be negative.");
                    }
                } catch (NumberFormatException e) {
                    throw new FlightBookingSystemException("Invalid price format. Please enter a number.");
                }
                System.out.print("Total Capacity: ");
                int totalCapacity;
                try {
                    totalCapacity = Integer.parseInt(reader.readLine());
                    if (totalCapacity <= 0) {
                        throw new FlightBookingSystemException("Total capacity must be a positive number.");
                    }
                } catch (NumberFormatException e) {
                    throw new FlightBookingSystemException("Invalid capacity format. Please enter an integer.");
                }

                System.out.print("Flight Type (BUDGET/COMMERCIAL): ");
                String flightTypeInput = reader.readLine().trim().toUpperCase();
                FlightType flightType;
                try {
                    flightType = FlightType.valueOf(flightTypeInput);
                } catch (IllegalArgumentException e) {
                    throw new FlightBookingSystemException("Invalid flight type. Must be BUDGET or COMMERCIAL.");
                }

                Map<CommercialClassType, Integer> classCapacities = null;
                if (flightType == FlightType.COMMERCIAL) {
                    System.out.print("Use custom class capacities? (yes/no - default is no): ");
                    String customCapacityChoice = reader.readLine().trim().toLowerCase();
                    if ("yes".equals(customCapacityChoice)) {
                        classCapacities = new HashMap<>();
                        int remainingCapacity = totalCapacity;
                        System.out.println("Enter capacities for each class. Remaining total capacity: " + remainingCapacity);
                        for (CommercialClassType classType : CommercialClassType.values()) {
                            System.out.print("Capacity for " + classType.name() + " (current remaining: " + remainingCapacity + "): ");
                            try {
                                int classCapacity = Integer.parseInt(reader.readLine());
                                if (classCapacity < 0) {
                                    throw new FlightBookingSystemException("Capacity cannot be negative.");
                                }
                                if (classCapacity > remainingCapacity) {
                                    throw new FlightBookingSystemException("Capacity for " + classType.name() + " exceeds remaining total capacity.");
                                }
                                classCapacities.put(classType, classCapacity);
                                remainingCapacity -= classCapacity;
                            } catch (NumberFormatException e) {
                                throw new FlightBookingSystemException("Invalid capacity format for " + classType.name() + ". Please enter an integer.");
                            }
                        }
                        if (remainingCapacity > 0) {
                            System.out.println("Warning: " + remainingCapacity + " capacity not assigned to any class.");
                        }
                    }
                }
                // AddFlight constructor no longer takes mealQuantities
                if (flightType == FlightType.BUDGET) {
                    return new AddFlight(flightNumber, origin, destination, departureDate, economyPrice, totalCapacity);
                } else { // Commercial
                    return new AddFlight(flightNumber, origin, destination, departureDate, economyPrice, totalCapacity, flightType, classCapacities);
                }

            case "addcustomer":
                System.out.print("Customer Name: ");
                String customerName = reader.readLine();
                System.out.print("Customer Phone: ");
                String customerPhone = reader.readLine();
                System.out.print("Customer Email: ");
                String customerEmail = reader.readLine();
                System.out.print("Customer Age: ");
                int customerAge;
                try {
                    customerAge = Integer.parseInt(reader.readLine());
                    if (customerAge < 0) {
                        throw new FlightBookingSystemException("Age cannot be negative.");
                    }
                } catch (NumberFormatException e) {
                    throw new FlightBookingSystemException("Invalid age format. Please enter an integer.");
                }
                System.out.print("Customer Gender (Male/Female/Other): ");
                String customerGender = reader.readLine();
                // NEW: Prompt for Preferred Meal Type
                System.out.print("Customer Preferred Meal Type (VEG/NON_VEG/VEGAN/GLUTEN_FREE/NONE - default NONE): ");
                String preferredMealTypeInput = reader.readLine().trim().toUpperCase();
                MealType preferredMealType;
                if (preferredMealTypeInput.isEmpty()) {
                    preferredMealType = MealType.NONE;
                } else {
                    try {
                        preferredMealType = MealType.valueOf(preferredMealTypeInput);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid preferred meal type. Defaulting to NONE.");
                        preferredMealType = MealType.NONE;
                    }
                }
                // Updated AddCustomer constructor call
                return new AddCustomer(customerName, customerPhone, customerEmail, customerAge, customerGender, preferredMealType);

            case "addmeal":
                System.out.print("Meal Name: ");
                String mealName = reader.readLine();
                System.out.print("Meal Description: ");
                String mealDescription = reader.readLine();
                System.out.print("Meal Price: ");
                BigDecimal mealPrice;
                try {
                    mealPrice = new BigDecimal(reader.readLine());
                    if (mealPrice.compareTo(BigDecimal.ZERO) < 0) {
                        throw new FlightBookingSystemException("Meal price cannot be negative.");
                    }
                } catch (NumberFormatException e) {
                    throw new FlightBookingSystemException("Invalid meal price format. Please enter a number.");
                }
                System.out.print("Meal Type (VEG/NON_VEG/VEGAN/GLUTEN_FREE/NONE): ");
                String mealTypeInput = reader.readLine().trim().toUpperCase();
                MealType mealType;
                try {
                    mealType = MealType.valueOf(mealTypeInput);
                } catch (IllegalArgumentException e) {
                    throw new FlightBookingSystemException("Invalid meal type. Must be one of: " + Arrays.toString(MealType.values()));
                }
                return new AddMeal(mealName, mealDescription, mealPrice, mealType);

            case "addbooking":
                Matcher matcher = ADDBOOKING_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    throw new FlightBookingSystemException("Usage: addbooking <customer ID> <outbound flight ID> [return flight ID]");
                }
                int customerId = Integer.parseInt(matcher.group(1));
                int outboundFlightId = Integer.parseInt(matcher.group(2));
                Integer returnFlightId = null;
                if (matcher.group(3) != null) {
                    returnFlightId = Integer.parseInt(matcher.group(3));
                }

                System.out.print("Enter desired booking class (ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST - default ECONOMY): ");
                String classInput = reader.readLine().trim().toUpperCase();
                CommercialClassType selectedClass;
                if (classInput.isEmpty()) {
                    selectedClass = CommercialClassType.ECONOMY;
                } else {
                    try {
                        selectedClass = CommercialClassType.valueOf(classInput);
                    } catch (IllegalArgumentException e) {
                        throw new FlightBookingSystemException("Invalid class type. Please choose from ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST.");
                    }
                }
                return new AddBooking(customerId, outboundFlightId, returnFlightId, selectedClass);

            case "cancelbooking":
                if (parts.length < 2) {
                    throw new FlightBookingSystemException("Usage: cancelbooking <customer ID> <flight ID>");
                }
                String[] cancelParts = parts[1].split(" ");
                if (cancelParts.length != 2) {
                    throw new FlightBookingSystemException("Usage: cancelbooking <customer ID> <flight ID>");
                }
                int cancelCustomerId = Integer.parseInt(cancelParts[0]);
                int cancelFlightId = Integer.parseInt(cancelParts[1]);
                return new CancelBooking(cancelCustomerId, cancelFlightId);
                
            case "editbooking":
                if (parts.length < 3) {
                    throw new FlightBookingSystemException("Usage: editbooking <customer ID> <flight ID>");
                }
                int editCustomerId = Integer.parseInt(parts[1]);
                int editFlightId = Integer.parseInt(parts[2]);
                // Commands now need the reader, so pass it to them
                return new EditBooking(editCustomerId, editFlightId);

            case "rebookflight":
                if (parts.length < 2) {
                    throw new FlightBookingSystemException("Usage: rebookflight <customer ID> <old flight ID> <new flight ID> <new class>");
                }
                String[] rebookParts = parts[1].split(" ");
                if (rebookParts.length != 4) {
                    throw new FlightBookingSystemException("Usage: rebookflight <customer ID> <old flight ID> <new flight ID> <new class>");
                }
                int rebookCustomerId = Integer.parseInt(rebookParts[0]);
                int oldFlightId = Integer.parseInt(rebookParts[1]);
                int newFlightId = Integer.parseInt(rebookParts[2]);
                CommercialClassType newClass;
                try {
                    newClass = CommercialClassType.valueOf(rebookParts[3].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new FlightBookingSystemException("Invalid new class. Please choose from ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST.");
                }
                return new RebookFlight(rebookCustomerId, oldFlightId, newFlightId, newClass);

            case "showflight":
                if (parts.length < 2) {
                    throw new FlightBookingSystemException("Usage: showflight <flight ID>");
                }
                int flightId = Integer.parseInt(parts[1]);
                return new ShowFlight(flightId);

            case "showcustomer":
                if (parts.length < 2) {
                    throw new FlightBookingSystemException("Usage: showcustomer <customer ID>");
                }
                customerId = Integer.parseInt(parts[1]);
                return new ShowCustomer(customerId);

            case "showmeal":
                if (parts.length < 2) {
                    throw new FlightBookingSystemException("Usage: showmeal <meal ID>");
                }
                int mealId = Integer.parseInt(parts[1]);
                return new ShowMeal(mealId);

            case "listflights":
                return new ListFlights();

            case "listallflights":
                return new ListAllFlights();

            case "listcustomers":
                return new ListCustomers();

            case "listallcustomers":
                return new ListAllCustomers();

            case "listmeals":
                return new ListMeals();

            case "listallmeals":
                return new ListAllMeals();

            case "removeflight":
                if (parts.length < 2) {
                    throw new FlightBookingSystemException("Usage: removeflight <flight ID>");
                }
                flightId = Integer.parseInt(parts[1]);
                return new RemoveFlight(flightId);

            case "removecustomer":
                if (parts.length < 2) {
                    throw new FlightBookingSystemException("Usage: removecustomer <customer ID>");
                }
                customerId = Integer.parseInt(parts[1]);
                return new RemoveCustomer(customerId);

            case "removemeal":
                if (parts.length < 2) {
                    throw new FlightBookingSystemException("Usage: removemeal <meal ID>");
                }
                mealId = Integer.parseInt(parts[1]);
                return new RemoveMeal(mealId);

            case "help":
                return new Help();

            case "exit":
                return new Exit();

            default:
                throw new FlightBookingSystemException("Unknown command.");
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
