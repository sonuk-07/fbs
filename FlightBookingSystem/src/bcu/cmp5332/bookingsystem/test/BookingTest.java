package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.model.MealType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingTest {

    private Customer customer;
    private Flight outboundFlight;
    private Flight returnFlight;
    private Meal meal;
    private LocalDate bookingDate;

    @BeforeEach
    void setUp() throws FlightBookingSystemException {
        // Mock a FlightBookingSystem to get a systemDate
        FlightBookingSystem fbs = new FlightBookingSystem();
        bookingDate = fbs.getSystemDate();

        customer = new Customer(1, "John Doe", "1234567890", "john@example.com", 30, "Male", MealType.NONE);
        
        // Outbound flight (Economy price used for booking price)
        outboundFlight = new Flight(101, "BA200", "London", "New York", bookingDate.plusDays(30), new BigDecimal("500.00"), 100, FlightType.COMMERCIAL, null);
        // Simulate adding passenger to update internal state for testing dynamic prices (though not directly used in Booking constructor, good practice)
        outboundFlight.addPassenger(customer, CommercialClassType.ECONOMY);

        // Return flight (Business class price used for booking price)
        returnFlight = new Flight(102, "BA201", "New York", "London", bookingDate.plusDays(45), new BigDecimal("500.00"), 100, FlightType.COMMERCIAL, null);
        returnFlight.addPassenger(customer, CommercialClassType.BUSINESS);

        meal = new Meal(1, "Vegetarian Meal", "Delicious veggie option", new BigDecimal("15.00"), MealType.VEG);
    }

    @Test
    void testBookingConstructorAndGetters_OneWayNoMeal() {
        Booking booking = new Booking(customer, outboundFlight, null, bookingDate, CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, null);

        assertEquals(customer, booking.getCustomer());
        assertEquals(outboundFlight, booking.getOutboundFlight());
        assertNull(booking.getReturnFlight());
        assertEquals(bookingDate, booking.getBookingDate());
        assertEquals(CommercialClassType.ECONOMY, booking.getBookedClass());
        assertEquals(new BigDecimal("500.00"), booking.getBookedPriceOutbound());
        assertEquals(BigDecimal.ZERO, booking.getBookedPriceReturn());
        assertNull(booking.getMeal());
        assertEquals(BigDecimal.ZERO, booking.getCancellationFee());
        assertEquals(BigDecimal.ZERO, booking.getRebookFee());
    }

    @Test
    void testBookingConstructorAndGetters_RoundTripWithMeal() throws FlightBookingSystemException {
        // Simulate dynamic prices that would be calculated by FBS
        BigDecimal outboundBookedPrice = outboundFlight.getDynamicPrice(CommercialClassType.ECONOMY, bookingDate);
        BigDecimal returnBookedPrice = returnFlight.getDynamicPrice(CommercialClassType.BUSINESS, bookingDate);

        Booking booking = new Booking(customer, outboundFlight, returnFlight, bookingDate, CommercialClassType.BUSINESS, outboundBookedPrice, returnBookedPrice, meal);

        assertEquals(customer, booking.getCustomer());
        assertEquals(outboundFlight, booking.getOutboundFlight());
        assertEquals(returnFlight, booking.getReturnFlight());
        assertEquals(bookingDate, booking.getBookingDate());
        assertEquals(CommercialClassType.BUSINESS, booking.getBookedClass());
        assertEquals(outboundBookedPrice, booking.getBookedPriceOutbound());
        assertEquals(returnBookedPrice, booking.getBookedPriceReturn());
        assertEquals(meal, booking.getMeal());
    }

    @Test
    void testSetters() {
        Booking booking = new Booking(customer, outboundFlight, null, bookingDate, CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, null);

        // Set cancellation fee
        BigDecimal cancellationFee = new BigDecimal("50.00");
        booking.setCancellationFee(cancellationFee);
        assertEquals(cancellationFee, booking.getCancellationFee());

        // Set rebook fee
        BigDecimal rebookFee = new BigDecimal("30.00");
        booking.setRebookFee(rebookFee);
        assertEquals(rebookFee, booking.getRebookFee());
    }

    @Test
    void testGetTotalBookingPrice_OneWayWithMeal() {
        Booking booking = new Booking(customer, outboundFlight, null, bookingDate, CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, meal);
        // Expected: 500.00 (outbound) + 15.00 (meal) = 515.00
        assertEquals(new BigDecimal("515.00"), booking.getTotalBookingPrice());
    }

    @Test
    void testGetTotalBookingPrice_RoundTripNoMeal() throws FlightBookingSystemException {
        // Outbound Economy base: 500.00. Return Business base: 500.00 * 2.5 = 1250.00
        BigDecimal outboundPrice = outboundFlight.getPriceForClass(CommercialClassType.ECONOMY);
        BigDecimal returnPrice = returnFlight.getPriceForClass(CommercialClassType.BUSINESS);
        Booking booking = new Booking(customer, outboundFlight, returnFlight, bookingDate, CommercialClassType.BUSINESS, outboundPrice, returnPrice, null);
        // Expected: 500.00 (outbound) + 1250.00 (return) = 1750.00
        assertEquals(new BigDecimal("1750.00"), booking.getTotalBookingPrice());
    }

    @Test
    void testGetDetailsShort_OneWayNoMeal() {
        Booking booking = new Booking(customer, outboundFlight, null, bookingDate, CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, null);
        String expected = "Booking: Outbound BA200, Return N/A - Class: Economy - Total Price: £500.00";
        assertEquals(expected, booking.getDetailsShort());
    }

    @Test
    void testGetDetailsShort_RoundTripWithMeal() throws FlightBookingSystemException {
        BigDecimal outboundPrice = outboundFlight.getPriceForClass(CommercialClassType.ECONOMY);
        BigDecimal returnPrice = returnFlight.getPriceForClass(CommercialClassType.BUSINESS);
        Booking booking = new Booking(customer, outboundFlight, returnFlight, bookingDate, CommercialClassType.BUSINESS, outboundPrice, returnPrice, meal);
        String expected = "Booking: Outbound BA200, Return BA201 - Class: Business, Meal: Vegetarian Meal (Vegetarian) - Total Price: £1765.00"; // 500 + 1250 + 15
        assertEquals(expected, booking.getDetailsShort());
    }

    @Test
    void testGetDetailsLong_OneWayWithMeal() {
        Booking booking = new Booking(customer, outboundFlight, null, bookingDate, CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, meal);
        String details = booking.getDetailsLong();

        assertTrue(details.contains("Customer: 1: John Doe (1234567890, john@example.com, 30, Male, Pref: None)"));
        assertTrue(details.contains("Outbound Flight: Flight #101 - BA200 - London to New York on " + bookingDate.plusDays(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " (COMMERCIAL)"));
        assertTrue(details.contains("Booked Class: Economy"));
        assertTrue(details.contains("Booked Price: £500.00"));
        assertFalse(details.contains("Return Flight:")); // No return flight
        assertTrue(details.contains("Meal: Vegetarian Meal (Type: Vegetarian, Price: £15.00)"));
        assertTrue(details.contains("Booking Date: " + bookingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        assertTrue(details.contains("Total Booking Price (Flights + Meal): £515.00"));
        assertFalse(details.contains("Cancellation Fee Applied:")); // No fee initially
        assertFalse(details.contains("Rebook Fee Applied:"));     // No fee initially
    }

    @Test
    void testGetDetailsLong_WithFeesApplied() throws FlightBookingSystemException {
        BigDecimal outboundPrice = outboundFlight.getPriceForClass(CommercialClassType.ECONOMY);
        BigDecimal returnPrice = returnFlight.getPriceForClass(CommercialClassType.BUSINESS);
        Booking booking = new Booking(customer, outboundFlight, returnFlight, bookingDate, CommercialClassType.BUSINESS, outboundPrice, returnPrice, meal);

        booking.setCancellationFee(new BigDecimal("100.00"));
        booking.setRebookFee(new BigDecimal("30.00"));

        String details = booking.getDetailsLong();
        assertTrue(details.contains("Cancellation Fee Applied: £100.00"));
        assertTrue(details.contains("Rebook Fee Applied: £30.00"));
    }
}