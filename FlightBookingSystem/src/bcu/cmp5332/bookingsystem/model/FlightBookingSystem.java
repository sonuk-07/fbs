package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FlightBookingSystem {

    private final LocalDate systemDate = LocalDate.now();

    private final Map<Integer, Customer> customers = new TreeMap<>();
    private final Map<Integer, Flight> flights = new TreeMap<>();
    private final Map<Integer, Meal> meals = new TreeMap<>();

    public LocalDate getSystemDate() {
        return systemDate;
    }

    private int nextFlightId = 1;
    private int nextCustomerId = 1;
    private int nextMealId = 1;

    public int generateNextFlightId() {
        while (flights.containsKey(nextFlightId)) {
            nextFlightId++;
        }
        return nextFlightId;
    }

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

    public List<Flight> getFlights() {
        List<Flight> out = new ArrayList<>();
        for (Flight flight : flights.values()) {
            if (!flight.isDeleted() && !flight.hasDeparted(systemDate)) {
                out.add(flight);
            }
        }
        return Collections.unmodifiableList(out);
    }

    public List<Flight> getAllFlights() {
        return Collections.unmodifiableList(new ArrayList<>(flights.values()));
    }

    public List<Customer> getCustomers() {
        return customers.values().stream()
                         .filter(customer -> !customer.isDeleted())
                         .collect(Collectors.toUnmodifiableList());
    }

    public List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(new ArrayList<>(customers.values()));
    }

    public List<Meal> getMeals() {
        return meals.values().stream()
                    .filter(meal -> !meal.isDeleted())
                    .collect(Collectors.toUnmodifiableList());
    }

    public List<Meal> getAllMeals() {
        return Collections.unmodifiableList(new ArrayList<>(meals.values()));
    }

    public List<Meal> getMealsFilteredByPreference(MealType preferredType) {
        if (preferredType == MealType.NONE) {
            return getMeals(); // Return all active meals if no preference
        }
        return meals.values().stream()
                    .filter(meal -> !meal.isDeleted() && meal.getType() == preferredType)
                    .collect(Collectors.toUnmodifiableList());
    }


    public Flight getFlightByID(int id) throws FlightBookingSystemException {
        Flight flight = flights.get(id);
        if (flight == null || flight.isDeleted() || flight.hasDeparted(systemDate)) {
            throw new FlightBookingSystemException("There is no active flight with that ID or it has departed.");
        }
        return flight;
    }

    public Flight getFlightByIDIncludingDeleted(int id) {
        return flights.get(id);
    }

    public Customer getCustomerByID(int id) throws FlightBookingSystemException {
        Customer customer = customers.get(id);
        if (customer == null || customer.isDeleted()) {
            throw new FlightBookingSystemException("There is no customer with that ID.");
        }
        return customer;
    }

    public Customer getCustomerByIDIncludingDeleted(int id) {
        return customers.get(id);
    }

    public Meal getMealByID(int id) throws FlightBookingSystemException {
        Meal meal = meals.get(id);
        if (meal == null || meal.isDeleted()) {
            throw new FlightBookingSystemException("There is no active meal with that ID.");
        }
        return meal;
    }

    public Meal getMealByIDIncludingDeleted(int id) {
        return meals.get(id);
    }

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

    public void addMeal(Meal meal) throws FlightBookingSystemException {
        if (meals.containsKey(meal.getId())) {
            throw new IllegalArgumentException("Duplicate meal ID.");
        }
        meals.put(meal.getId(), meal);
    }

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
        
        Booking booking = new Booking(customer, outbound, returnFlight, systemDate, bookedClass, outboundBookedPrice, returnBookedPrice, selectedMeal);

        customer.addBooking(booking);
        outbound.addPassenger(customer, bookedClass);
        if (returnFlight != null) {
            returnFlight.addPassenger(customer, bookedClass);
        }
    }

    public void cancelBooking(Customer customer, Flight flight) throws FlightBookingSystemException {
        Booking bookingToCancel = null;
        for (Booking booking : customer.getBookings()) {
            if (booking.getOutboundFlight().equals(flight) || (booking.getReturnFlight() != null && booking.getReturnFlight().equals(flight))) {
                bookingToCancel = booking;
                break;
            }
        }

        if (bookingToCancel == null) {
            throw new FlightBookingSystemException("Customer does not have a booking for this flight.");
        }

        BigDecimal cancellationFee = calculateCancellationFee(bookingToCancel);
        bookingToCancel.setCancellationFee(cancellationFee);
        
        customer.cancelBookingForFlight(flight.getId());
        
        bookingToCancel.getOutboundFlight().removePassenger(customer, bookingToCancel.getBookedClass());
        if (bookingToCancel.getReturnFlight() != null) {
            bookingToCancel.getReturnFlight().removePassenger(customer, bookingToCancel.getBookedClass());
        }

        System.out.println("Booking for Flight " + flight.getFlightNumber() + " cancelled. Cancellation Fee: £" + cancellationFee);
    }

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

    // Changed access modifier from private to public
    public BigDecimal calculateRebookFee(Booking booking, Flight newFlight, CommercialClassType newClass) {
        return new BigDecimal("30.00").setScale(2, RoundingMode.HALF_UP);
    }

    public boolean removeFlightById(int flightId) throws FlightBookingSystemException {
        Flight flight = flights.get(flightId);
        if (flight == null) {
            return false;
        }
        flight.setDeleted(true);
        return true;
    }

    public boolean removeCustomerById(int customerId) throws FlightBookingSystemException {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            return false;
        }
        customer.setDeleted(true);
        return true;
    }

    public boolean removeMealById(int mealId) throws FlightBookingSystemException {
        Meal meal = meals.get(mealId);
        if (meal == null) {
            return false;
        }
        meal.setDeleted(true);
        return true;
    }
}
