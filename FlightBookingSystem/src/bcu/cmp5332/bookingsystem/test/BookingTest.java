package bcu.cmp5332.bookingsystem.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.model.MealType;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingTest {

    private Customer testCustomer;
    private Flight outboundFlight;
    private Flight returnFlight;
    private Meal testMeal;
    private LocalDate bookingDate;

    @BeforeEach
    void setUp() {
        // Mock dependencies needed for Booking
        // For Customer, we only need its ID and Name for getDetailsShort() in Booking's long details
        testCustomer = new Customer(1, "John Doe", "1234567890", "john.doe@example.com", 30, "Male", MealType.NON_VEG);

        // For Flight, we need ID, flight number, origin, destination, departure date, and price for getDetailsShort()
        outboundFlight = new Flight(101, "BA2490", "London", "New York",
                LocalDate.of(2025, 7, 10), new BigDecimal("500.00"), 150);
        returnFlight = new Flight(102, "BA2491", "New York", "London",
                LocalDate.of(2025, 7, 20), new BigDecimal("450.00"), 150);

        // For Meal, we need ID, name, price, and type for getDetailsShort()
        testMeal = new Meal(1, "Chicken Curry", "Spicy chicken with rice", new BigDecimal("15.00"), MealType.NON_VEG);

        bookingDate = LocalDate.of(2025, 6, 1);
    }

    @Test
    void testBookingFullConstructorOneWayNoMeal() {
        // Test first constructor with one-way flight and no meal
        Booking booking = new Booking(1, testCustomer, outboundFlight, null, bookingDate,
                CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, null);

        assertNotNull(booking);
        assertEquals(1, booking.getId());
        assertEquals(testCustomer, booking.getCustomer());
        assertEquals(outboundFlight, booking.getOutboundFlight());
        assertNull(booking.getReturnFlight());
        assertEquals(bookingDate, booking.getBookingDate());
        assertEquals(CommercialClassType.ECONOMY, booking.getBookedClass());
        assertEquals(new BigDecimal("500.00"), booking.getBookedPriceOutbound());
        assertEquals(BigDecimal.ZERO, booking.getBookedPriceReturn());
        assertNull(booking.getMeal());
        assertEquals(BigDecimal.ZERO, booking.getCancellationFee());
        assertEquals(BigDecimal.ZERO, booking.getRebookFee());
        assertFalse(booking.isCancelled());
    }

    @Test
    void testBookingFullConstructorRoundTripWithMeal() {
        // Test first constructor with round-trip flight and a meal
        Booking booking = new Booking(2, testCustomer, outboundFlight, returnFlight, bookingDate,
                CommercialClassType.BUSINESS, new BigDecimal("1250.00"), new BigDecimal("1125.00"), testMeal);

        assertNotNull(booking);
        assertEquals(2, booking.getId());
        assertEquals(testCustomer, booking.getCustomer());
        assertEquals(outboundFlight, booking.getOutboundFlight());
        assertEquals(returnFlight, booking.getReturnFlight());
        assertEquals(bookingDate, booking.getBookingDate());
        assertEquals(CommercialClassType.BUSINESS, booking.getBookedClass());
        assertEquals(new BigDecimal("1250.00"), booking.getBookedPriceOutbound());
        assertEquals(new BigDecimal("1125.00"), booking.getBookedPriceReturn());
        assertEquals(testMeal, booking.getMeal());
        assertEquals(BigDecimal.ZERO, booking.getCancellationFee());
        assertEquals(BigDecimal.ZERO, booking.getRebookFee());
        assertFalse(booking.isCancelled());
    }

    @Test
    void testBookingSimpleConstructor() {
        // Test second constructor (simple one-way)
        Booking booking = new Booking(3, testCustomer, outboundFlight, bookingDate, CommercialClassType.ECONOMY);

        assertNotNull(booking);
        assertEquals(3, booking.getId());
        assertEquals(testCustomer, booking.getCustomer());
        assertEquals(outboundFlight, booking.getOutboundFlight());
        assertNull(booking.getReturnFlight());
        assertEquals(bookingDate, booking.getBookingDate());
        assertEquals(CommercialClassType.ECONOMY, booking.getBookedClass());
        assertEquals(BigDecimal.ZERO, booking.getBookedPriceOutbound()); // Assumed ZERO by simple constructor
        assertEquals(BigDecimal.ZERO, booking.getBookedPriceReturn());   // Assumed ZERO by simple constructor
        assertNull(booking.getMeal()); // Assumed NULL by simple constructor
        assertEquals(BigDecimal.ZERO, booking.getCancellationFee());
        assertEquals(BigDecimal.ZERO, booking.getRebookFee());
        assertFalse(booking.isCancelled());
    }

    @Test
    void testGettersAndSetters() {
        Booking booking = new Booking(4, testCustomer, outboundFlight, null, bookingDate,
                CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, null);

        // Test setters
        LocalDate newBookingDate = LocalDate.of(2025, 6, 2);
        booking.setBookingDate(newBookingDate);
        assertEquals(newBookingDate, booking.getBookingDate());

        booking.setBookedClass(CommercialClassType.PREMIUM_ECONOMY);
        assertEquals(CommercialClassType.PREMIUM_ECONOMY, booking.getBookedClass());

        BigDecimal newOutboundPrice = new BigDecimal("750.00");
        booking.setBookedPriceOutbound(newOutboundPrice);
        assertEquals(newOutboundPrice, booking.getBookedPriceOutbound());

        BigDecimal newReturnPrice = new BigDecimal("600.00");
        booking.setBookedPriceReturn(newReturnPrice);
        assertEquals(newReturnPrice, booking.getBookedPriceReturn());

        BigDecimal newCancellationFee = new BigDecimal("50.00");
        booking.setCancellationFee(newCancellationFee);
        assertEquals(newCancellationFee, booking.getCancellationFee());

        BigDecimal newRebookFee = new BigDecimal("30.00");
        booking.setRebookFee(newRebookFee);
        assertEquals(newRebookFee, booking.getRebookFee());

        Meal newMeal = new Meal(2, "Veggie Burger", "Plant-based burger", new BigDecimal("12.00"), MealType.VEG);
        booking.setMeal(newMeal);
        assertEquals(newMeal, booking.getMeal());

        assertFalse(booking.isCancelled());
        booking.setCancelled(true);
        assertTrue(booking.isCancelled());
    }

    @Test
    void testGetTotalBookingPriceOneWayNoMeal() {
        Booking booking = new Booking(5, testCustomer, outboundFlight, null, bookingDate,
                CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, null);
        assertEquals(new BigDecimal("500.00").setScale(2, BigDecimal.ROUND_HALF_UP), booking.getTotalBookingPrice());
    }

    @Test
    void testGetTotalBookingPriceOneWayWithMeal() {
        Booking booking = new Booking(6, testCustomer, outboundFlight, null, bookingDate,
                CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, testMeal);
        // 500.00 (outbound) + 15.00 (meal) = 515.00
        assertEquals(new BigDecimal("515.00").setScale(2, BigDecimal.ROUND_HALF_UP), booking.getTotalBookingPrice());
    }

    @Test
    void testGetTotalBookingPriceRoundTripNoMeal() {
        Booking booking = new Booking(7, testCustomer, outboundFlight, returnFlight, bookingDate,
                CommercialClassType.BUSINESS, new BigDecimal("1250.00"), new BigDecimal("1125.00"), null);
        // 1250.00 (outbound) + 1125.00 (return) = 2375.00
        assertEquals(new BigDecimal("2375.00").setScale(2, BigDecimal.ROUND_HALF_UP), booking.getTotalBookingPrice());
    }

    @Test
    void testGetTotalBookingPriceRoundTripWithMeal() {
        Booking booking = new Booking(8, testCustomer, outboundFlight, returnFlight, bookingDate,
                CommercialClassType.FIRST, new BigDecimal("2000.00"), new BigDecimal("1800.00"), testMeal);
        // 2000.00 (outbound) + 1800.00 (return) + 15.00 (meal) = 3815.00
        assertEquals(new BigDecimal("3815.00").setScale(2, BigDecimal.ROUND_HALF_UP), booking.getTotalBookingPrice());
    }

    @Test
    void testGetDetailsShortOneWayNoMeal() {
        Booking booking = new Booking(9, testCustomer, outboundFlight, null, bookingDate,
                CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, null);
        String expected = "Booking ID: 9 - Outbound BA2490, Return N/A - Class: Economy - Total Price: £500.00";
        assertEquals(expected, booking.getDetailsShort());
    }

    @Test
    void testGetDetailsShortRoundTripWithMeal() {
        Booking booking = new Booking(10, testCustomer, outboundFlight, returnFlight, bookingDate,
                CommercialClassType.BUSINESS, new BigDecimal("1250.00"), new BigDecimal("1125.00"), testMeal);
        String expected = "Booking ID: 10 - Outbound BA2490, Return BA2491 - Class: Business, Meal: Chicken Curry (Non-Vegetarian) - Total Price: £2390.00";
        assertEquals(expected, booking.getDetailsShort());
    }

    @Test
    void testGetDetailsShortCancelledBooking() {
        Booking booking = new Booking(11, testCustomer, outboundFlight, null, bookingDate,
                CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, null);
        booking.setCancelled(true);
        String expected = "Booking ID: 11 (CANCELLED) - Outbound BA2490, Return N/A - Class: Economy - Total Price: £500.00";
        assertEquals(expected, booking.getDetailsShort());
    }


    @Test
    void testGetDetailsLongOneWayNoMealNoFees() {
        Booking booking = new Booking(12, testCustomer, outboundFlight, null, bookingDate,
                CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, null);
        String expected = "Booking ID: 12\n" +
                          "Status: ACTIVE\n" +
                          "Customer: 1: John Doe (1234567890, john.doe@example.com, 30, Male, Pref: Non-Vegetarian)\n" +
                          "Outbound Flight: Flight #101 - BA2490 - London to New York on 10/07/2025 (BUDGET)\n" +
                          "  Booked Class: Economy\n" +
                          "  Booked Price: £500.00\n" +
                          "Meal: None\n" +
                          "Booking Date: 01/06/2025\n" +
                          "Total Booking Price (Flights + Meal): £500.00";
        assertEquals(expected, booking.getDetailsLong());
    }

    @Test
    void testGetDetailsLongRoundTripWithMealWithFees() {
        Booking booking = new Booking(13, testCustomer, outboundFlight, returnFlight, bookingDate,
                CommercialClassType.BUSINESS, new BigDecimal("1250.00"), new BigDecimal("1125.00"), testMeal);
        booking.setCancellationFee(new BigDecimal("200.00"));
        booking.setRebookFee(new BigDecimal("30.00"));

        String expected = "Booking ID: 13\n" +
                          "Status: ACTIVE\n" +
                          "Customer: 1: John Doe (1234567890, john.doe@example.com, 30, Male, Pref: Non-Vegetarian)\n" +
                          "Outbound Flight: Flight #101 - BA2490 - London to New York on 10/07/2025 (BUDGET)\n" +
                          "  Booked Class: Business\n" +
                          "  Booked Price: £1250.00\n" +
                          "Return Flight: Flight #102 - BA2491 - New York to London on 20/07/2025 (BUDGET)\n" +
                          "  Booked Price: £1125.00\n" +
                          "Meal: Chicken Curry (Type: Non-Vegetarian, Price: £15.00)\n" +
                          "Booking Date: 01/06/2025\n" +
                          "Total Booking Price (Flights + Meal): £2390.00\n" +
                          "Cancellation Fee Applied: £200.00\n" +
                          "Rebook Fee Applied: £30.00";
        assertEquals(expected, booking.getDetailsLong());
    }

    @Test
    void testGetDetailsLongCancelledBookingStatus() {
        Booking booking = new Booking(14, testCustomer, outboundFlight, null, bookingDate,
                CommercialClassType.ECONOMY, new BigDecimal("500.00"), BigDecimal.ZERO, null);
        booking.setCancelled(true);

        String expected = "Booking ID: 14\n" +
                          "Status: CANCELLED\n" + // Status should be CANCELLED
                          "Customer: 1: John Doe (1234567890, john.doe@example.com, 30, Male, Pref: Non-Vegetarian)\n" +
                          "Outbound Flight: Flight #101 - BA2490 - London to New York on 10/07/2025 (BUDGET)\n" +
                          "  Booked Class: Economy\n" +
                          "  Booked Price: £500.00\n" +
                          "Meal: None\n" +
                          "Booking Date: 01/06/2025\n" +
                          "Total Booking Price (Flights + Meal): £500.00";
        assertEquals(expected, booking.getDetailsLong());
    }
}
