package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FlightBookingSystem {

    private final LocalDate systemDate = LocalDate.now();// Current system date

    private final Map<Integer, Customer> customers = new TreeMap<>();
    private final Map<Integer, Flight> flights = new TreeMap<>();

    public LocalDate getSystemDate() {
        return systemDate;
    }

    private int nextFlightId = 1;
    private int nextCustomerId = 1;

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

    // MODIFIED: getFlights() now filters for future flights and non-deleted flights
    public List<Flight> getFlights() {
        List<Flight> out = new ArrayList<>();
        for (Flight flight : flights.values()) {
            // Only add flights that are not deleted AND have not departed
            if (!flight.isDeleted() && !flight.hasDeparted(systemDate)) {
                out.add(flight);
            }
        }
        return Collections.unmodifiableList(out);
    }

    // Returns all flights, including deleted ones (used for data persistence)
    public List<Flight> getAllFlights() {
        return Collections.unmodifiableList(new ArrayList<>(flights.values()));
    }

    public List<Customer> getCustomers() {
        return customers.values().stream()
                         .filter(customer -> !customer.isDeleted())
                         .collect(Collectors.toUnmodifiableList());
    }

    // Returns all customers, including deleted ones (used for data persistence)
    public List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(new ArrayList<>(customers.values()));
    }

    // getFlightByID still filters for active, non-deleted flights for general use
    public Flight getFlightByID(int id) throws FlightBookingSystemException {
        Flight flight = flights.get(id);
        if (flight == null || flight.isDeleted() || flight.hasDeparted(systemDate)) {
            throw new FlightBookingSystemException("There is no active flight with that ID or it has departed.");
        }
        return flight;
    }

    // Used for internal logic or admin functions where deleted/departed flights might be needed
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
            // If ID already exists, update the existing customer (e.g., loaded from file)
            customers.put(customer.getId(), customer);
        } else {
            customers.put(customer.getId(), customer);
            if (customer.getId() >= nextCustomerId) {
                nextCustomerId = customer.getId() + 1;
            }
        }
    }

    // MODIFIED: addBooking method to incorporate capacity checks and dynamic pricing
    public void addBooking(Customer customer, Flight outbound, Flight returnFlight, CommercialClassType bookedClass)
            throws FlightBookingSystemException {

        // --- Capacity Check ---
        // Check outbound flight capacity
        if (!outbound.hasAnySeatsLeft()) { // General check for total flight capacity
            throw new FlightBookingSystemException("Outbound flight " + outbound.getFlightNumber() + " is at full capacity.");
        }
        if (!outbound.isClassAvailable(bookedClass)) { // Specific class capacity check
            throw new FlightBookingSystemException("Outbound flight " + outbound.getFlightNumber() + " has no seats left in " + bookedClass.getClassName() + " class.");
        }

        // Check return flight capacity if applicable
        if (returnFlight != null) {
            if (!returnFlight.hasAnySeatsLeft()) {
                throw new FlightBookingSystemException("Return flight " + returnFlight.getFlightNumber() + " is at full capacity.");
            }
            if (!returnFlight.isClassAvailable(bookedClass)) {
                throw new FlightBookingSystemException("Return flight " + returnFlight.getFlightNumber() + " has no seats left in " + bookedClass.getClassName() + " class.");
            }
        }

        // --- Dynamic Price Calculation ---
        BigDecimal outboundBookedPrice = outbound.getDynamicPrice(bookedClass, systemDate);
        BigDecimal returnBookedPrice = (returnFlight != null) ? returnFlight.getDynamicPrice(bookedClass, systemDate) : BigDecimal.ZERO;
        
        // --- Create Booking ---
        Booking booking = new Booking(customer, outbound, returnFlight, systemDate, bookedClass, outboundBookedPrice, returnBookedPrice);

        // --- Add Booking and Passengers ---
        customer.addBooking(booking);
        outbound.addPassenger(customer, bookedClass); // Pass the booked class to addPassenger
        if (returnFlight != null) {
            returnFlight.addPassenger(customer, bookedClass); // Pass the booked class
        }
    }

    // MODIFIED: cancelBooking to incorporate cancellation fee
    public void cancelBooking(Customer customer, Flight flight) throws FlightBookingSystemException {
        // Find the specific booking to cancel
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

        // Apply cancellation fee
        BigDecimal cancellationFee = calculateCancellationFee(bookingToCancel);
        bookingToCancel.setCancellationFee(cancellationFee); // Set the fee on the booking object
        
        customer.cancelBookingForFlight(flight.getId()); // This removes the booking from customer's list
        
        // Remove passenger from flight(s) and decrement occupied seats
        bookingToCancel.getOutboundFlight().removePassenger(customer, bookingToCancel.getBookedClass());
        if (bookingToCancel.getReturnFlight() != null) {
            bookingToCancel.getReturnFlight().removePassenger(customer, bookingToCancel.getBookedClass());
        }

        System.out.println("Booking for Flight " + flight.getFlightNumber() + " cancelled. Cancellation Fee: £" + cancellationFee);
    }

    // NEW: Helper method to calculate cancellation fee
    private BigDecimal calculateCancellationFee(Booking booking) {
        // Example: Flat fee of £20, or a percentage of the total price
        BigDecimal totalBookedPrice = booking.getBookedPriceOutbound().add(booking.getBookedPriceReturn());
        BigDecimal fee = totalBookedPrice.multiply(new BigDecimal("0.10")); // 10% cancellation fee
        if (fee.compareTo(new BigDecimal("20.00")) < 0) { // Minimum fee of £20
            fee = new BigDecimal("20.00");
        }
        return fee.setScale(2, RoundingMode.HALF_UP);
    }

    public void rebookFlight(Customer customer, Booking booking, Flight newFlight, CommercialClassType newClass) throws FlightBookingSystemException {

        BigDecimal rebookFee = calculateRebookFee(booking, newFlight, newClass);
        booking.setRebookFee(rebookFee); // Assuming you'll add setRebookFee to Booking.java
        System.out.println("Rebooking in progress. Rebook Fee: £" + rebookFee);
        // ... (actual rebooking logic would go here)
    }

    // NEW: Helper method to calculate rebook fee
    private BigDecimal calculateRebookFee(Booking booking, Flight newFlight, CommercialClassType newClass) {
        // Example: Flat fee of £30
        return new BigDecimal("30.00").setScale(2, RoundingMode.HALF_UP);
    }


    public boolean removeFlightById(int flightId) throws FlightBookingSystemException {
        Flight flight = flights.get(flightId);
        if (flight == null) {
            return false;
        }
        flight.setDeleted(true);
        // Also need to handle active bookings for this flight:
        // Option 1: Automatically cancel them with a fee.
        // Option 2: Mark them as 'impacted' and require customer to rebook/cancel.
        // For simplicity, for now, we'll assume they just disappear from customer's list
        // when getBookings() in Customer filters by active flights, or handle explicitly.
        // A more robust system would update booking status.
        return true;
    }

    public boolean removeCustomerById(int customerId) throws FlightBookingSystemException {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            return false;
        }
        customer.setDeleted(true);
        // If a customer is deleted, their active bookings should probably be cancelled.
        // For now, we're not explicitly cancelling, but getBookings() in Customer
        // might filter by active bookings, or you might implement cascade deletion/cancellation.
        return true;
    }
}