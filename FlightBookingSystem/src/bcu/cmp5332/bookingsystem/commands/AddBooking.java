package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.model.MealType;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Command to add a new booking to the flight booking system.
 * This command handles the selection of flights (outbound and optional return),
 * commercial class, and meal preferences, calculates the total price,
 * and confirms the booking with the user.
 */
public class AddBooking implements Command {
    private final int customerId;
    private final int outboundFlightId;
    private final Integer returnFlightId;
    private final CommercialClassType selectedClass;
    
    /**
     * Constructor for a one-way flight booking.
     * @param customerId The ID of the customer making the booking.
     * @param outboundFlightId The ID of the outbound flight.
     * @param selectedClass The commercial class type for the booking.
     */
    public AddBooking(int customerId, int outboundFlightId, CommercialClassType selectedClass) {
        this(customerId, outboundFlightId, null, selectedClass);
    }

    
    /**
     * Constructor for a round-trip flight booking.
     * @param customerId The ID of the customer making the booking.
     * @param outboundFlightId The ID of the outbound flight.
     * @param returnFlightId The ID of the return flight (can be null for one-way).
     * @param selectedClass The commercial class type for the booking.
     */
    public AddBooking(int customerId, int outboundFlightId, Integer returnFlightId, CommercialClassType selectedClass) {
        this.customerId = customerId;
        this.outboundFlightId = outboundFlightId;
        this.returnFlightId = returnFlightId;
        this.selectedClass = selectedClass;
    }

    /**
     * Executes the AddBooking command.
     * This method performs several steps:
     * 1. Retrieves customer and flight details.
     * 2. Performs pre-booking checks (class availability, seat capacity).
     * 3. Calculates and displays dynamic flight prices.
     * 4. Guides the user through meal selection, filtering by customer preference.
     * 5. Displays a summary of booking details, including total estimated price.
     * 6. Prompts for user confirmation before finalizing the booking.
     * 7. Adds the new booking to the system if confirmed.
     *
     * @param flightBookingSystem The FlightBookingSystem instance.
     * @param reader The BufferedReader for user input.
     * @throws FlightBookingSystemException If any business rule is violated (e.g., flight not found, no seats)
     * or if there's an error during input/output operations.
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem, BufferedReader reader) throws FlightBookingSystemException {
        try {
            Customer customer = flightBookingSystem.getCustomerByID(customerId);
            Flight outbound = flightBookingSystem.getFlightByID(outboundFlightId);
            Flight returnFlight = null;
            if (returnFlightId != null) {
                returnFlight = flightBookingSystem.getFlightByID(returnFlightId);
            }

            if (!outbound.isClassAvailable(selectedClass)) {
                throw new FlightBookingSystemException("Selected class is not available on outbound flight " + outbound.getFlightNumber() + " (ID: " + outbound.getId() + ").");
            }
            if (!outbound.hasAnySeatsLeft()) {
                throw new FlightBookingSystemException("Outbound flight " + outbound.getFlightNumber() + " (ID: " + outbound.getId() + ") is at full capacity.");
            }

            BigDecimal outboundPrice = outbound.getDynamicPrice(selectedClass, flightBookingSystem.getSystemDate());
            BigDecimal returnPrice = BigDecimal.ZERO;
            if (returnFlight != null) {
                if (!returnFlight.isClassAvailable(selectedClass)) {
                    throw new FlightBookingSystemException("Selected class is not available on return flight " + returnFlight.getFlightNumber() + " (ID: " + returnFlight.getId() + ").");
                }
                if (!returnFlight.hasAnySeatsLeft()) {
                    throw new FlightBookingSystemException("Return flight " + returnFlight.getFlightNumber() + " (ID: " + returnFlight.getId() + ") is at full capacity.");
                }
                returnPrice = returnFlight.getDynamicPrice(selectedClass, flightBookingSystem.getSystemDate());
            }

            Meal selectedMeal = null;
            MealType preferredMealType = customer.getPreferredMealType();
            List<Meal> availableMeals = flightBookingSystem.getMealsFilteredByPreference(preferredMealType);

            if (!availableMeals.isEmpty()) {
                System.out.println("\n--- Available Meal Options (Filtered by Customer Preference: " + preferredMealType.getDisplayName() + ") ---");
                if (preferredMealType == MealType.NONE) {
                    System.out.println("Customer has no preferred meal type. Showing all available meals.");
                }

                for (Meal meal : availableMeals) {
                    System.out.println(meal.getDetailsShort());
                }
                System.out.print("Enter Meal ID (or 0 for no meal): ");
                String mealInput = reader.readLine().trim();
                try {
                    int mealId = Integer.parseInt(mealInput);
                    if (mealId != 0) {
                        selectedMeal = flightBookingSystem.getMealByID(mealId);
                        if (preferredMealType != MealType.NONE && selectedMeal.getType() != preferredMealType) {
                            System.out.println("Warning: Selected meal type (" + selectedMeal.getType().getDisplayName() + ") does not match customer's preference (" + preferredMealType.getDisplayName() + "). Proceeding anyway.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid meal ID format. No meal selected.");
                } catch (FlightBookingSystemException e) {
                    System.out.println("Error selecting meal: " + e.getMessage() + ". No meal selected.");
                }
            } else {
                System.out.println("\nNo meal options available matching customer's preference.");
            }

            System.out.println("\n--- Booking Details ---");
            System.out.println("Customer: " + customer.getName() + " (ID: " + customer.getId() + ")");
            System.out.println("Outbound Flight: " + outbound.getFlightNumber() + " (ID: " + outbound.getId() + ") - " + outbound.getOrigin() + " to " + outbound.getDestination());
            System.out.println("  Class: " + selectedClass.getClassName());
            System.out.println("  Price: £" + outboundPrice);

            if (returnFlight != null) {
                System.out.println("Return Flight: " + returnFlight.getFlightNumber() + " (ID: " + returnFlight.getId() + ") - " + returnFlight.getOrigin() + " to " + returnFlight.getDestination());
                System.out.println("  Class: " + selectedClass.getClassName());
                System.out.println("  Price: £" + returnPrice);
            }

            BigDecimal totalFlightPrice = outboundPrice.add(returnPrice);
            BigDecimal mealPrice = (selectedMeal != null) ? selectedMeal.getPrice() : BigDecimal.ZERO;
            BigDecimal totalBookingPrice = totalFlightPrice.add(mealPrice);

            System.out.println("Selected Meal: " + (selectedMeal != null ? selectedMeal.getName() + " (" + selectedMeal.getType().getDisplayName() + ")" : "None") + " (£" + mealPrice + ")");
            System.out.println("Total Estimated Booking Price (Flights + Meal): £" + totalBookingPrice.setScale(2, RoundingMode.HALF_UP));

            System.out.print("Confirm booking? (yes/no): ");
            String confirmation = reader.readLine().trim().toLowerCase();

            if (!"yes".equals(confirmation)) {
                System.out.println("Booking cancelled by user.");
                return;
            }

            flightBookingSystem.addBooking(customer, outbound, returnFlight, selectedClass, selectedMeal);

            System.out.println("Booking completed for Customer #" + customerId + " in " + selectedClass.getClassName() +
                               " on Flight " + outbound.getFlightNumber() +
                               (returnFlight != null ? " and Return Flight " + returnFlight.getFlightNumber() : "") +
                               (selectedMeal != null ? " with " + selectedMeal.getName() + " meal" : "") + ".");

        } catch (IOException e) {
            throw new FlightBookingSystemException("Error reading input during booking confirmation: " + e.getMessage());
        }
    }
}