package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.CommercialClassType;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Meal;
import bcu.cmp5332.bookingsystem.model.MealType; // Ensure this enum is correctly defined in your project

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CustomerTest {

    private Customer customer;
    private Flight flight1;
    private Flight flight2;
    private Meal meal;
    private LocalDate systemDate;

    @BeforeEach
    void setUp() {
        systemDate = LocalDate.now();
        // Assuming MealType.VEG is the correct enum constant for Vegetarian
        customer = new Customer(1, "Alice Smith", "07123456789", "alice@email.com", 25, "Female", MealType.VEG);
        // Mock flights for testing bookings; actual prices are not critical for Customer class tests
        flight1 = new Flight(101, "BA100", "London", "Paris", systemDate.plusDays(10), new BigDecimal("100.00"), 50);
        flight2 = new Flight(102, "BA101", "Paris", "London", systemDate.plusDays(15), new BigDecimal("100.00"), 50);
        meal = new Meal(1, "Standard Meal", "A standard meal", new BigDecimal("10.00"), MealType.NON_VEG);
    }

    @Test
    void testCustomerConstructorAndGetters() {
        assertEquals(1, customer.getId());
        assertEquals("Alice Smith", customer.getName());
        assertEquals("07123456789", customer.getPhone());
        assertEquals("alice@email.com", customer.getEmail());
        assertEquals(25, customer.getAge());
        assertEquals("Female", customer.getGender());
        // Asserting against MealType.VEG as set in setUp()
        assertEquals(MealType.VEG, customer.getPreferredMealType());
        assertFalse(customer.isDeleted());
        assertTrue(customer.getBookings().isEmpty());
    }

    @Test
    void testCustomerSetters() {
        customer.setName("Alicia Jones");
        assertEquals("Alicia Jones", customer.getName());
        customer.setPhone("07987654321");
        assertEquals("07987654321", customer.getPhone());
        customer.setEmail("alicia@example.com");
        assertEquals("alicia@example.com", customer.getEmail());
        customer.setAge(26);
        assertEquals(26, customer.getAge());
        customer.setGender("Other");
        assertEquals("Other", customer.getGender());
        customer.setPreferredMealType(MealType.VEGAN); // Setting to another MealType
        assertEquals(MealType.VEGAN, customer.getPreferredMealType());
        customer.setDeleted(true);
        assertTrue(customer.isDeleted());
    }

    @Test
    void testAddBooking() {
        Booking booking1 = new Booking(customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking1);
        List<Booking> bookings = customer.getBookings();
        assertEquals(1, bookings.size());
        assertTrue(bookings.contains(booking1));

        Booking booking2 = new Booking(customer, flight2, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, null);
        customer.addBooking(booking2);
        assertEquals(2, customer.getBookings().size());
        assertTrue(customer.getBookings().contains(booking2));
    }

    @Test
    void testCancelBookingForFlight_OneWay() {
        Booking booking = new Booking(customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking);
        assertTrue(customer.hasBookingForFlight(flight1));
        assertEquals(1, customer.getBookings().size());

        customer.cancelBookingForFlight(flight1.getId());
        assertFalse(customer.hasBookingForFlight(flight1));
        assertTrue(customer.getBookings().isEmpty());
    }

    @Test
    void testCancelBookingForFlight_RoundTrip_Outbound() {
        Booking booking = new Booking(customer, flight1, flight2, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), new BigDecimal("100.00"), meal);
        customer.addBooking(booking);
        assertTrue(customer.hasBookingForFlight(flight1));
        assertTrue(customer.hasBookingForFlight(flight2));
        assertEquals(1, customer.getBookings().size());

        // Cancelling the outbound flight should remove the entire booking
        customer.cancelBookingForFlight(flight1.getId());
        assertFalse(customer.hasBookingForFlight(flight1));
        assertFalse(customer.hasBookingForFlight(flight2));
        assertTrue(customer.getBookings().isEmpty());
    }

    @Test
    void testCancelBookingForFlight_RoundTrip_Return() {
        Booking booking = new Booking(customer, flight1, flight2, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), new BigDecimal("100.00"), meal);
        customer.addBooking(booking);
        assertTrue(customer.hasBookingForFlight(flight1));
        assertTrue(customer.hasBookingForFlight(flight2));
        assertEquals(1, customer.getBookings().size());

        // Cancelling the return flight should also remove the entire booking
        customer.cancelBookingForFlight(flight2.getId());
        assertFalse(customer.hasBookingForFlight(flight1));
        assertFalse(customer.hasBookingForFlight(flight2));
        assertTrue(customer.getBookings().isEmpty());
    }

    @Test
    void testCancelBookingForFlight_NonExistentFlight() {
        Booking booking = new Booking(customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking);
        assertEquals(1, customer.getBookings().size());

        customer.cancelBookingForFlight(999); // Non-existent flight ID
        assertEquals(1, customer.getBookings().size()); // Booking should still be there
    }

    @Test
    void testHasBookingForFlight() {
        assertFalse(customer.hasBookingForFlight(flight1));

        Booking booking1 = new Booking(customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking1);
        assertTrue(customer.hasBookingForFlight(flight1));
        assertFalse(customer.hasBookingForFlight(flight2)); // No booking for flight2 yet

        Booking booking2 = new Booking(customer, flight2, flight1, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), new BigDecimal("100.00"), null);
        customer.addBooking(booking2);
        // Customer now has two bookings: booking1 for flight1, and booking2 which has flight2 as outbound and flight1 as return.
        // So customer.hasBookingForFlight(flight1) should remain true due to booking1, and booking2 for return flight.
        assertTrue(customer.hasBookingForFlight(flight2));
        assertTrue(customer.hasBookingForFlight(flight1));
    }

    @Test
    void testGetDetailsShort() {
        // This assumes MealType.VEG.getDisplayName() returns "Vegetarian"
        String expected = "1: Alice Smith (07123456789, alice@email.com, 25, Female, Pref: Vegetarian)";
        assertEquals(expected, customer.getDetailsShort());
    }

    @Test
    void testGetDetailsLong_NoBookings() {
        String details = customer.getDetailsLong();
        assertTrue(details.contains("Customer ID: 1"));
        assertTrue(details.contains("Name: Alice Smith"));
        assertTrue(details.contains("Phone: 07123456789"));
        assertTrue(details.contains("Email: alice@email.com"));
        assertTrue(details.contains("Age: 25"));
        assertTrue(details.contains("Gender: Female"));
        // This assumes MealType.VEG.getDisplayName() returns "Vegetarian"
        assertTrue(details.contains("Preferred Meal Type: Vegetarian"));
        assertTrue(details.contains("Bookings: 0"));
        assertFalse(details.contains("- Outbound Flight ID:"));
    }

    @Test
    void testGetDetailsLong_WithBookings() {
        // Note: The getDetailsLong method in Customer does not display meal price or total price from Booking.
        // It displays Flight ID, Booking Date, and Class.
        Booking booking1 = new Booking(customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking1);

        String details = customer.getDetailsLong();
        assertTrue(details.contains("Bookings: 1"));
        assertTrue(details.contains("- Outbound Flight ID: " + flight1.getId()));
        // LocalDate.toString() formats as "yyyy-MM-dd" by default, unless specified in the customer class
        assertTrue(details.contains(", Date: " + systemDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        assertTrue(details.contains(", Class: Economy"));
        // The Customer's getDetailsLong() does not include specific meal details or total price from the booking,
        // so these assertions should be false.
        assertFalse(details.contains("Meal:"));
        assertFalse(details.contains("Total Price:"));
    }

    @Test
    void testGetDetailsLong_WithMultipleBookings() {
        Booking booking1 = new Booking(customer, flight1, null, systemDate, CommercialClassType.ECONOMY, new BigDecimal("100.00"), BigDecimal.ZERO, meal);
        customer.addBooking(booking1);

        // Simulate a second booking for a different flight
        Booking booking2 = new Booking(customer, flight2, null, systemDate.minusDays(5), CommercialClassType.ECONOMY, new BigDecimal("120.00"), BigDecimal.ZERO, null);
        customer.addBooking(booking2);

        String details = customer.getDetailsLong();
        assertTrue(details.contains("Bookings: 2"));
        assertTrue(details.contains("- Outbound Flight ID: " + flight1.getId()));
        assertTrue(details.contains("- Outbound Flight ID: " + flight2.getId()));
    }
}