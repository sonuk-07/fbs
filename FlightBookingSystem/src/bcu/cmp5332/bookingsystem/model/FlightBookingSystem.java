package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Manages the operations of the flight booking system, including managing flights,
 * customers, meals, and bookings.
 */


public class FlightBookingSystem {

    private final LocalDate systemDate = LocalDate.now();

    private final Map<Integer, Customer> customers = new TreeMap<>();
    private final Map<Integer, Flight> flights = new TreeMap<>();
    private final Map<Integer, Meal> meals = new TreeMap<>();
    private final Map<Integer, Booking> bookings = new TreeMap<>(); // ADDED: Map to store bookings

    public LocalDate getSystemDate() {
        return systemDate;
    }

    private int nextFlightId = 1;
    private int nextCustomerId = 1;
    private int nextMealId = 1;
    private int nextBookingId = 1; // ADDED: Next booking ID

    /**
     * Generates the next available unique flight ID.
     *
     * @return The next available flight ID.
     */


    public int generateNextFlightId() {
        while (flights.containsKey(nextFlightId)) {
            nextFlightId++;
        }
        return nextFlightId;
    }


    /**
     * Generates the next available unique customer ID.
     *
     * @return The next available customer ID.
     */


    public int generateNextCustomerId() {
        while (customers.containsKey(nextCustomerId)) {
            nextCustomerId++;
        }
        return nextCustomerId;
    }

    public int generateNextMealId() {
        while (meals.containsKey(nextMealId)) {
            nextMealId++;
        }
        return nextMealId;
    }

    // ADDED: Booking ID generation
    public int generateNextBookingId() {
        while (bookings.containsKey(nextBookingId)) {
            nextBookingId++;
        }
        return nextBookingId;
    }

    // ADDED: Setter for nextBookingId (for use in data loading)
    public void setNextBookingId(int id) {
        this.nextBookingId = id;
    }

    /**
     * Returns an unmodifiable list of active (not deleted and not departed) flights.
     *
     * @return A list of active flights.
     */

    public List<Flight> getFlights() {
        List<Flight> out = new ArrayList<>();
        for (Flight flight : flights.values()) {
            if (!flight.isDeleted() && !flight.hasDeparted(systemDate)) {
                out.add(flight);
            }
        }
        return Collections.unmodifiableList(out);
    }


    /**
     * Returns an unmodifiable list of all flights, including deleted and departed ones.
     *
     * @return A list of all flights.
     */

    public List<Flight> getAllFlights() {
        return Collections.unmodifiableList(new ArrayList<>(flights.values()));
    }

    /**
     * Returns an unmodifiable list of active (not deleted) customers.
     *
     * @return A list of active customers.
     */


    public List<Customer> getCustomers() {
        return customers.values().stream()
                .filter(customer -> !customer.isDeleted())
                .collect(Collectors.toUnmodifiableList());
    }


    /**
     * Returns an unmodifiable list of all customers, including deleted ones.
     *
     * @return A list of all customers.
     */

    public List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(new ArrayList<>(customers.values()));
    }

    /**
     * Returns an unmodifiable list of active (not deleted) meals.
     *
     * @return A list of active meals.
     */

    public List<Meal> getMeals() {
        return meals.values().stream()
                .filter(meal -> !meal.isDeleted())
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns an unmodifiable list of all meals, including deleted ones.
     *
     * @return A list of all meals.
     */


    public List<Meal> getAllMeals() {
        return Collections.unmodifiableList(new ArrayList<>(meals.values()));
    }

    /**
     * Returns a list of active meals filtered by a preferred meal type.
     * If the preferred type is {@link MealType#NONE}, all active meals are returned.
     *
     * @param preferredType The preferred meal type to filter by.
     * @return An unmodifiable list of meals matching the preference, or all active meals if preference is NONE.
     */

    public List<Meal> getMealsFilteredByPreference(MealType preferredType) {
        if (preferredType == MealType.NONE) {
            return getMeals(); // Return all active meals if no preference
        }
        return meals.values().stream()
                .filter(meal -> !meal.isDeleted() && meal.getType() == preferredType)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Retrieves an active flight by its ID.
     *
     * @param id The ID of the flight to retrieve.
     * @return The Flight object.
     * @throws FlightBookingSystemException If no active flight with the given ID exists or it has departed.
     */

    public Flight getFlightByID(int id) throws FlightBookingSystemException {
        Flight flight = flights.get(id);
        if (flight == null || flight.isDeleted() || flight.hasDeparted(systemDate)) {
            throw new FlightBookingSystemException("There is no active flight with that ID or it has departed.");
        }
        return flight;
    }

    /**
     * Retrieves a flight by its ID, including those marked as deleted.
     *
     * @param id The ID of the flight to retrieve.
     * @return The Flight object, or null if not found.
     */


    public Flight getFlightByIDIncludingDeleted(int id) {
        return flights.get(id);
    }

    /**
     * Retrieves an active customer by their ID.
     *
     * @param id The ID of the customer to retrieve.
     * @return The Customer object.
     * @throws FlightBookingSystemException If no active customer with the given ID exists.
     */

    public Customer getCustomerByID(int id) throws FlightBookingSystemException {
        Customer customer = customers.get(id);
        if (customer == null || customer.isDeleted()) {
            throw new FlightBookingSystemException("There is no customer with that ID.");
        }
        return customer;
    }

    /**
     * Retrieves a customer by their ID, including those marked as deleted.
     *
     * @param id The ID of the customer to retrieve.
     * @return The Customer object, or null if not found.
     */


    public Customer getCustomerByIDIncludingDeleted(int id) {
        return customers.get(id);
    }

    /**
     * Retrieves an active meal by its ID.
     *
     * @param id The ID of the meal to retrieve.
     * @return The Meal object.
     * @throws FlightBookingSystemException If no active meal with the given ID exists.
     */

    public Meal getMealByID(int id) throws FlightBookingSystemException {
        Meal meal = meals.get(id);
        if (meal == null || meal.isDeleted()) {
            throw new FlightBookingSystemException("There is no active meal with that ID.");
        }
        return meal;
    }

    /**
     * Retrieves a meal by its ID, including those marked as deleted.
     *
     * @param id The ID of the meal to retrieve.
     * @return The Meal object, or null if not found.
     */


    public Meal getMealByIDIncludingDeleted(int id) {
        return meals.get(id);
    }

    /**
     * Adds a new flight to the system.
     *
     * @param flight The Flight object to add.
     * @throws FlightBookingSystemException If a flight with the same flight number and departure date already exists.
     * @throws IllegalArgumentException If a flight with a duplicate ID already exists.
     */

    public void addFlight(Flight flight) throws FlightBookingSystemException {
        if (flights.containsKey(flight.getId())) {
            throw new IllegalArgumentException("Duplicate flight ID.");
        }
        for (Flight existing : flights.values()) {
            if (existing.getFlightNumber().equals(flight.getFlightNumber())
                    && existing.getDepartureDate().isEqual(flight.getDepartureDate())) {
                throw new FlightBookingSystemException("There is a flight with same "
                        + "number and departure date in the system");
            }
        }
        flights.put(flight.getId(), flight);
    }

    /**
     * Adds a new customer to the system or updates an existing one if the ID already exists.
     *
     * @param customer The Customer object to add.
     */


    public void addCustomer(Customer customer) {
        if (customers.containsKey(customer.getId())) {
            customers.put(customer.getId(), customer);
        } else {
            customers.put(customer.getId(), customer);
            if (customer.getId() >= nextCustomerId) {
                nextCustomerId = customer.getId() + 1;
            }
        }
    }

    /**
     * Adds a new meal to the system.
     *
     * @param meal The Meal object to add.
     * @throws IllegalArgumentException If a meal with a duplicate ID already exists.
     */

    public void addMeal(Meal meal) throws FlightBookingSystemException {
        if (meals.containsKey(meal.getId())) {
            throw new IllegalArgumentException("Duplicate meal ID.");
        }
        meals.put(meal.getId(), meal);
    }

    /**
     * Adds a new booking to the system for a customer.
     *
     * @param customer The customer making the booking.
     * @param outbound The outbound flight for the booking.
     * @param returnFlight The return flight for the booking (can be null for one-way).
     * @param bookedClass The commercial class type for the booking.
     * @param selectedMeal The selected meal for the booking.
     * @throws FlightBookingSystemException If any flight is at full capacity or the chosen class is not available.
     */

    public void addBooking(Customer customer, Flight outbound, Flight returnFlight, CommercialClassType bookedClass, Meal selectedMeal)
            throws FlightBookingSystemException {

        if (!outbound.hasAnySeatsLeft()) {
            throw new FlightBookingSystemException("Outbound flight " + outbound.getFlightNumber() + " is at full capacity.");
        }
        if (!outbound.isClassAvailable(bookedClass)) {
            throw new FlightBookingSystemException("Outbound flight " + outbound.getFlightNumber() + " has no seats left in " + bookedClass.getClassName() + " class.");
        }

        if (returnFlight != null) {
            if (!returnFlight.hasAnySeatsLeft()) {
                throw new FlightBookingSystemException("Return flight " + returnFlight.getFlightNumber() + " is at full capacity.");
            }
            if (!returnFlight.isClassAvailable(bookedClass)) {
                throw new FlightBookingSystemException("Return flight " + returnFlight.getFlightNumber() + " has no seats left in " + bookedClass.getClassName() + " class.");
            }
        }

        BigDecimal outboundBookedPrice = outbound.getDynamicPrice(bookedClass, systemDate);
        BigDecimal returnBookedPrice = (returnFlight != null) ? returnFlight.getDynamicPrice(bookedClass, systemDate) : BigDecimal.ZERO;

        // Generate a new booking ID
        int newBookingId = generateNextBookingId();

        // Pass the generated ID to the Booking constructor
        Booking booking = new Booking(newBookingId, customer, outbound, returnFlight, systemDate, bookedClass, outboundBookedPrice, returnBookedPrice, selectedMeal);

        customer.addBooking(booking);
        outbound.addPassenger(customer, bookedClass);
        if (returnFlight != null) {
            returnFlight.addPassenger(customer, bookedClass);
        }
        bookings.put(booking.getId(), booking); // ADDED: Add booking to the system's map
        System.out.println("Booking ID " + booking.getId() + " created for customer " + customer.getName() + ".");
    }

    /**
     * ADDED: A method to add a booking without re-adding passengers.
     * This is primarily for use during data loading where passenger counts are already managed.
     * @param booking The Booking object to add.
     */
    public void addBookingWithoutFlightUpdate(Booking booking) {
        bookings.put(booking.getId(), booking);
        // Customer.addBooking(booking) is usually done by the data manager or main addBooking method.
        // It's assumed the customer object already has this booking linked or will be linked externally.
    }


    /**
     * Cancels a booking for a specific customer and flight.
     * A cancellation fee is calculated and applied.
     *
     * @param customer The customer whose booking is to be cancelled.
     * @param flight The flight associated with the booking to be cancelled.
     * @throws FlightBookingSystemException If the customer does not have a booking for the specified flight, or it's already cancelled.
     */

    public void cancelBooking(Customer customer, Flight flight) throws FlightBookingSystemException {
        Booking bookingToCancel = null;
        for (Booking booking : customer.getBookings()) {
            if (!booking.isCancelled() && // Only consider active bookings
                ((booking.getOutboundFlight().equals(flight) && !booking.getOutboundFlight().isDeleted()) ||
                 (booking.getReturnFlight() != null && booking.getReturnFlight().equals(flight) && !booking.getReturnFlight().isDeleted()))) {
                bookingToCancel = booking;
                break;
            }
        }

        if (bookingToCancel == null) {
            throw new FlightBookingSystemException("No active booking found for customer " + customer.getName() + " on flight " + flight.getFlightNumber());
        }

        // Calculate cancellation fee
        BigDecimal cancellationFee = calculateCancellationFee(bookingToCancel);
        bookingToCancel.setCancellationFee(cancellationFee);

        // Mark the booking as cancelled
        bookingToCancel.setCancelled(true);

        // Remove customer from the specific flight segment's passenger list
        if (bookingToCancel.getOutboundFlight().equals(flight)) {
            bookingToCancel.getOutboundFlight().removePassenger(customer, bookingToCancel.getBookedClass());
        } else if (bookingToCancel.getReturnFlight() != null && bookingToCancel.getReturnFlight().equals(flight)) {
            bookingToCancel.getReturnFlight().removePassenger(customer, bookingToCancel.getBookedClass());
        }

        // Note: The `customer.cancelBookingForFlight(flight.getId());` line
        // in your original code seems to be trying to *remove* the booking
        // from the customer's list. With `isCancelled` flag, you typically
        // just mark it as cancelled, not remove it from the list entirely,
        // for historical/audit purposes. If `customer.cancelBookingForFlight`
        // actually sets the `isCancelled` flag on the booking, that's fine.
        // If it removes it, you might want to reconsider that for auditing.

        System.out.println("Booking ID " + bookingToCancel.getId() + " for Flight " + flight.getFlightNumber() + " cancelled. Cancellation Fee: £" + cancellationFee);
    }


    /**
     * Calculates the cancellation fee for a given booking.
     * The fee is 10% of the total booked price (flights + meal), with a minimum of £20.00.
     *
     * @param booking The booking for which to calculate the cancellation fee.
     * @return The calculated cancellation fee, rounded to two decimal places.
     */

    public BigDecimal calculateCancellationFee(Booking booking) {
        BigDecimal totalBookedPrice = booking.getBookedPriceOutbound().add(booking.getBookedPriceReturn());
        if (booking.getMeal() != null) {
            totalBookedPrice = totalBookedPrice.add(booking.getMeal().getPrice());
        }
        BigDecimal fee = totalBookedPrice.multiply(new BigDecimal("0.10"));
        if (fee.compareTo(new BigDecimal("20.00")) < 0) {
            fee = new BigDecimal("20.00");
        }
        return fee.setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * Calculates the rebooking fee for a booking.
     * Currently, this is a fixed fee of £30.00.
     *
     * @param booking The original booking.
     * @param newFlight The new flight for rebooking.
     * @param newClass The new commercial class for rebooking.
     * @return The rebooking fee, rounded to two decimal places.
     */

    // Changed access modifier from private to public
    public BigDecimal calculateRebookFee(Booking booking, Flight newFlight, CommercialClassType newClass) {
        return new BigDecimal("30.00").setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Removes a flight from the system by marking it as deleted.
     *
     * @param flightId The ID of the flight to remove.
     * @return true if the flight was found and marked as deleted, false otherwise.
     * @throws FlightBookingSystemException If an error occurs during removal (though currently, no specific exceptions are thrown by this method).
     */

    public boolean removeFlightById(int flightId) throws FlightBookingSystemException {
        Flight flight = flights.get(flightId);
        if (flight == null) {
            return false;
        }
        flight.setDeleted(true);
        return true;
    }
    /**
     * Removes a customer from the system by marking them as deleted.
     *
     * @param customerId The ID of the customer to remove.
     * @return true if the customer was found and marked as deleted, false otherwise.
     * @throws FlightBookingSystemException If an error occurs during removal (though currently, no specific exceptions are thrown by this method).
     */

    public boolean removeCustomerById(int customerId) throws FlightBookingSystemException {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            return false;
        }
        customer.setDeleted(true);
        // Also ensure any bookings associated with this customer are handled
        // For simplicity, we are just marking customer as deleted.
        // In a real system, you might want to cancel their active bookings too.
        return true;
    }

    /**
     * Removes a meal from the system by marking it as deleted.
     *
     * @param mealId The ID of the meal to remove.
     * @return true if the meal was found and marked as deleted, false otherwise.
     * @throws FlightBookingSystemException If an error occurs during removal (though currently, no specific exceptions are thrown by this method).
     */


    public boolean removeMealById(int mealId) throws FlightBookingSystemException {
        Meal meal = meals.get(mealId);
        if (meal == null) {
            return false;
        }
        meal.setDeleted(true);
        return true;
    }
    public void editBooking(Customer customer, Flight flightInBooking, LocalDate newBookingDate, CommercialClassType newClass) throws FlightBookingSystemException {
        Booking bookingToEdit = null;
        // Find the booking associated with the customer and the flightInBooking
        for (Booking booking : customer.getBookings()) {
            // Check if the booking is active and relates to the flightInBooking
            if (!booking.isCancelled() &&
                ((booking.getOutboundFlight() != null && booking.getOutboundFlight().getId() == flightInBooking.getId()) ||
                 (booking.getReturnFlight() != null && booking.getReturnFlight().getId() == flightInBooking.getId()))) {
                bookingToEdit = booking;
                break;
            }
        }

        if (bookingToEdit == null) {
            throw new FlightBookingSystemException("No active booking for Customer ID " + customer.getId() + " on Flight ID " + flightInBooking.getId() + " found.");
        }

        if (newClass != null && newClass != bookingToEdit.getBookedClass()) {
            // Check if new class is available and has seats
            if (!flightInBooking.isClassAvailable(newClass)) {
                throw new FlightBookingSystemException("New class " + newClass.getClassName() + " is not available on flight " + flightInBooking.getFlightNumber());
            }
            if (!flightInBooking.isClassAvailable(newClass)) { // Use hasSeatsAvailableForClass
                throw new FlightBookingSystemException("No seats available for " + newClass.getClassName() + " on flight " + flightInBooking.getFlightNumber());
            }

            // Remove passenger from old class and add to new class
            flightInBooking.removePassenger(customer, bookingToEdit.getBookedClass());
            flightInBooking.addPassenger(customer, newClass);
            bookingToEdit.setBookedClass(newClass);
        }

        // Update booking date if different
        if (newBookingDate != null && !newBookingDate.isEqual(bookingToEdit.getBookingDate())) {
            bookingToEdit.setBookingDate(newBookingDate);
        }

        // Recalculate prices based on current system date and new class
        BigDecimal newOutboundPrice = bookingToEdit.getOutboundFlight().getDynamicPrice(bookingToEdit.getBookedClass(), systemDate);
        bookingToEdit.setBookedPriceOutbound(newOutboundPrice);

        if (bookingToEdit.getReturnFlight() != null) {
            BigDecimal newReturnPrice = bookingToEdit.getReturnFlight().getDynamicPrice(bookingToEdit.getBookedClass(), systemDate);
            bookingToEdit.setBookedPriceReturn(newReturnPrice);
        }

        System.out.println("Booking ID " + bookingToEdit.getId() + " for customer " + customer.getName() + " on flight " + flightInBooking.getFlightNumber() + " updated.");
    }

    /**
     * Returns an unmodifiable list of all bookings in the system.
     * @return A list of all bookings.
     */
    public List<Booking> getBookings() {
        return Collections.unmodifiableList(new ArrayList<>(bookings.values()));
    }

    /**
     * Returns a booking by its ID.
     * @param id The ID of the booking to retrieve.
     * @return The Booking object, or null if not found.
     */
    public Booking getBookingByID(int id) {
        return bookings.get(id);
    }
}